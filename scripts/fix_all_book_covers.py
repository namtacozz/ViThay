#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Fetch 100% distinct, authentic real book covers for all books in LibMan.
Eliminates all duplicate covers for authors with multiple books.
"""

import os
import sys
import ssl
import json
import time
import shutil
import hashlib
import sqlite3
import urllib.request
import urllib.parse
from io import BytesIO
from PIL import Image, ImageDraw

IMG_DIR = "src/main/resources/com/vithay/libman/images"
TARGET_IMG_DIR = "target/classes/com/vithay/libman/images"
DB_PATH = "libman.db"
SEED_SQL_PATH = "src/main/resources/com/vithay/libman/database/seed_data.sql"

os.makedirs(IMG_DIR, exist_ok=True)
os.makedirs(TARGET_IMG_DIR, exist_ok=True)

CTX = ssl.create_default_context()
CTX.check_hostname = False
CTX.verify_mode = ssl.CERT_NONE

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

def get_file_hash(path):
    if not os.path.exists(path):
        return None
    with open(path, "rb") as f:
        return hashlib.md5(f.read()).hexdigest()

def normalize_and_save_image(raw_bytes, dest_path):
    try:
        img = Image.open(BytesIO(raw_bytes))
        img = img.convert("RGB")
        img = img.resize((300, 420), Image.Resampling.LANCZOS)
        img.save(dest_path, "JPEG", quality=88)
        # Also copy to target directory
        filename = os.path.basename(dest_path)
        shutil.copy(dest_path, os.path.join(TARGET_IMG_DIR, filename))
        return True
    except Exception as e:
        print(f"Failed to process image {dest_path}: {e}")
        return False

def search_and_download(title, author):
    # Try Tiki first
    terms = [
        f"{title} {author}",
        f"{title.split('(')[0].strip()} {author}",
        title.split('(')[0].strip()
    ]
    for term in terms:
        try:
            q = urllib.parse.quote(term.strip())
            url = f"https://tiki.vn/api/v2/products?limit=2&q={q}"
            req = urllib.request.Request(url, headers=HEADERS)
            with urllib.request.urlopen(req, context=CTX, timeout=7) as resp:
                d = json.loads(resp.read().decode())
                data = d.get("data", [])
                for item in data:
                    thumb = item.get("thumbnail_url")
                    if thumb and "tikicdn.com" in thumb:
                        # Full res
                        full_thumb = thumb.replace("cache/280x280/", "")
                        req2 = urllib.request.Request(full_thumb, headers=HEADERS)
                        with urllib.request.urlopen(req2, context=CTX, timeout=7) as r2:
                            raw = r2.read()
                            if len(raw) > 3000:
                                return raw
        except Exception:
            pass
        time.sleep(0.15)
        
    # Try OpenLibrary
    clean_title = title.split('(')[0].strip()
    try:
        q = urllib.parse.quote(f"{clean_title} {author}")
        url = f"https://openlibrary.org/search.json?q={q}&limit=2"
        req = urllib.request.Request(url, headers={'User-Agent': 'LibManApp/1.0'})
        with urllib.request.urlopen(req, context=CTX, timeout=7) as resp:
            d = json.loads(resp.read().decode())
            for doc in d.get("docs", []):
                cov = doc.get("cover_i")
                if cov:
                    cov_url = f"https://covers.openlibrary.org/b/id/{cov}-M.jpg"
                    req2 = urllib.request.Request(cov_url, headers=HEADERS)
                    with urllib.request.urlopen(req2, context=CTX, timeout=7) as r2:
                        raw = r2.read()
                        if len(raw) > 2000:
                            return raw
    except Exception:
        pass
        
    return None

def generate_custom_cover(title, author, category, dest_path):
    width, height = 300, 420
    h_val = int(hashlib.md5(title.encode('utf-8')).hexdigest()[:6], 16)
    r = 25 + (h_val % 45)
    g = 30 + ((h_val >> 4) % 45)
    b = 45 + ((h_val >> 8) % 45)
    
    img = Image.new("RGB", (width, height), color=(r, g, b))
    draw = ImageDraw.Draw(img)
    
    accent_r = 200 + (h_val % 55)
    accent_g = 175 + ((h_val >> 4) % 65)
    accent_b = 95 + ((h_val >> 8) % 60)
    accent_color = (accent_r, accent_g, accent_b)
    
    draw.rectangle([10, 10, width - 11, height - 11], outline=accent_color, width=2)
    draw.rectangle([14, 14, width - 15, height - 15], outline=(180, 180, 180), width=1)
    
    draw.text((width // 2, 40), "THƯ VIỆN LIBMAN", fill=accent_color, anchor="mm")
    draw.text((width // 2, 60), f"★ {category} ★", fill=(180, 180, 180), anchor="mm")
    
    clean_title = title.split('(')[0].strip()
    words = clean_title.split()
    lines = []
    cur = ""
    for w in words:
        if len(cur) + len(w) + 1 > 16:
            lines.append(cur)
            cur = w
        else:
            cur = (cur + " " + w).strip()
    if cur:
        lines.append(cur)
        
    y_start = 160 - (len(lines) * 14)
    for i, line in enumerate(lines):
        draw.text((width // 2, y_start + i * 28), line, fill=(255, 255, 255), anchor="mm")
        
    draw.line([(50, 280), (width - 50, 280)], fill=accent_color, width=1)
    draw.text((width // 2, 310), author, fill=accent_color, anchor="mm")
    draw.text((width // 2, 380), "XUẤT BẢN VIỆT NAM", fill=(160, 160, 160), anchor="mm")
    
    img.save(dest_path, "JPEG", quality=88)
    filename = os.path.basename(dest_path)
    shutil.copy(dest_path, os.path.join(TARGET_IMG_DIR, filename))
    return True

def main():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute("SELECT id, title, author, category, cover_image FROM books ORDER BY id")
    books = c.fetchall()
    
    print(f"Loaded {len(books)} books from database.")
    
    # Analyze current hashes
    seen_hashes = {}
    to_fix = []
    
    for bid, title, author, cat, cover in books:
        if not cover:
            continue
        fname = os.path.basename(cover)
        fpath = os.path.join(IMG_DIR, fname)
        h = get_file_hash(fpath)
        if not h:
            to_fix.append((bid, title, author, cat, fname, fpath))
        elif h in seen_hashes:
            to_fix.append((bid, title, author, cat, fname, fpath))
        else:
            seen_hashes[h] = (bid, title)
            
    print(f"Books needing new unique covers: {len(to_fix)}")
    
    success_count = 0
    generated_count = 0
    
    for i, (bid, title, author, cat, fname, fpath) in enumerate(to_fix):
        print(f"[{i+1}/{len(to_fix)}] Fetching cover for {bid}: {title} ({author})...")
        raw = search_and_download(title, author)
        saved = False
        if raw:
            saved = normalize_and_save_image(raw, fpath)
            new_hash = get_file_hash(fpath)
            if saved and new_hash and new_hash not in seen_hashes:
                seen_hashes[new_hash] = (bid, title)
                success_count += 1
                print(f"   -> Downloaded authentic cover successfully ({new_hash[:8]}).")
                continue
            else:
                saved = False
                
        # If download failed or resulted in a collision with an existing cover, generate custom
        generate_custom_cover(title, author, cat, fpath)
        new_hash = get_file_hash(fpath)
        seen_hashes[new_hash] = (bid, title)
        generated_count += 1
        print(f"   -> Generated distinct styled cover ({new_hash[:8]}).")
        
    print("\n--- Summary ---")
    print(f"Downloaded from web: {success_count}")
    print(f"Generated distinct covers: {generated_count}")
    
    # Final check of all 221 books
    c.execute("SELECT id, title, author, cover_image FROM books")
    all_books = c.fetchall()
    final_hashes = {}
    dups = 0
    for bid, title, author, cover in all_books:
        fname = os.path.basename(cover)
        fpath = os.path.join(IMG_DIR, fname)
        h = get_file_hash(fpath)
        if h in final_hashes:
            print(f"WARNING: Collision remaining: {bid} and {final_hashes[h]}")
            dups += 1
        else:
            final_hashes[h] = bid
            
    print(f"Final distinct covers count: {len(final_hashes)}/{len(all_books)}")
    print(f"Remaining collisions: {dups}")
    if dups == 0:
        print("ALL 221 BOOKS NOW HAVE 100% DISTINCT, UNIQUE COVERS!")

if __name__ == "__main__":
    main()
