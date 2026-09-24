#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Download 100% authentic, real book covers from the web (Bing Images & Tiki).
Strictly NO synthetic/generated placeholder images.
"""

import os
import sys
import re
import time
import hashlib
import sqlite3
import requests
import urllib.parse
from PIL import Image, ImageStat
from io import BytesIO

IMG_DIR = "src/main/resources/com/vithay/libman/images"
TARGET_IMG_DIR = "target/classes/com/vithay/libman/images"
DB_PATH = "libman.db"

os.makedirs(IMG_DIR, exist_ok=True)
os.makedirs(TARGET_IMG_DIR, exist_ok=True)

session = requests.Session()
session.headers.update({
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8',
    'Accept-Language': 'vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7',
})

def get_file_hash(path):
    if not os.path.exists(path):
        return None
    with open(path, "rb") as f:
        return hashlib.md5(f.read()).hexdigest()

def is_synthetic_cover(fpath):
    if not os.path.exists(fpath):
        return True
    try:
        img = Image.open(fpath).convert('RGB')
        w, h = img.size
        if w == 300 and h == 420:
            p10 = img.getpixel((10, 10))
            p5 = img.getpixel((5, 5))
            p_bg = img.getpixel((150, 200))
            if p10 != p5 and abs(p5[0] - p_bg[0]) < 5 and abs(p5[1] - p_bg[1]) < 5:
                return True
        stat = ImageStat.Stat(img)
        if max(stat.var) < 25:
            return True
    except Exception:
        return True
    return False

def search_bing_images(query):
    url = f"https://www.bing.com/images/search?q={urllib.parse.quote(query)}&first=1"
    try:
        r = session.get(url, timeout=10)
        if r.status_code == 200:
            murls = re.findall(r'murl&quot;:&quot;(http[^&]+)&quot;', r.text)
            return murls
    except Exception as e:
        print(f"   [Bing Error] {e}")
    return []

def search_tiki_images(query):
    url = f"https://tiki.vn/api/v2/products?q={urllib.parse.quote(query)}"
    try:
        r = session.get(url, timeout=8, headers={'Referer': 'https://tiki.vn/'})
        if r.status_code == 200:
            data = r.json().get('data', [])
            urls = []
            for item in data:
                thumb = item.get('thumbnail_url')
                if thumb:
                    full_res = thumb.replace('cache/280x280/', '')
                    urls.append(full_res)
            return urls
    except Exception:
        pass
    return []

def download_and_validate_image(url):
    bad_keywords = ['icon', 'logo', 'banner', 'avatar', 'placeholder', '.svg', '.gif']
    if any(k in url.lower() for k in bad_keywords):
        return None
        
    try:
        r = session.get(url, timeout=8, headers={'User-Agent': 'Mozilla/5.0'})
        if r.status_code != 200 or len(r.content) < 8000:
            return None
            
        img = Image.open(BytesIO(r.content))
        w, h = img.size
        if w < 180 or h < 180:
            return None
            
        stat = ImageStat.Stat(img.convert('RGB'))
        if max(stat.var) < 30:
            return None
            
        img = img.convert('RGB')
        img = img.resize((300, 420), Image.Resampling.LANCZOS)
        
        buf = BytesIO()
        img.save(buf, format='JPEG', quality=92)
        return buf.getvalue()
    except Exception:
        return None

def fetch_real_cover_for_book(title, author, seen_hashes):
    clean_title = re.sub(r'\s*\([^)]*\)', '', title).strip()
    
    eng_match = re.search(r'\(([^)]+)\)', title)
    eng_title = eng_match.group(1) if eng_match else ""
    
    query_candidates = [
        f"bìa sách {clean_title} {author}",
        f"bìa sách {clean_title}",
        f"sách {clean_title} {author}",
    ]
    if eng_title:
        query_candidates.append(f"book cover {eng_title} {author}")
        query_candidates.append(f"bìa sách {eng_title}")
    query_candidates.append(f"{clean_title} {author} nxb")
    
    for q in query_candidates:
        candidate_urls = search_bing_images(q)
        if not candidate_urls:
            candidate_urls = search_tiki_images(q)
            
        for img_url in candidate_urls[:12]:
            raw_bytes = download_and_validate_image(img_url)
            if raw_bytes:
                h = hashlib.md5(raw_bytes).hexdigest()
                if h not in seen_hashes:
                    return raw_bytes, img_url, h
                    
        time.sleep(0.2)
        
    return None, None, None

def main():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute("SELECT id, title, author, category, cover_image FROM books ORDER BY id")
    books = c.fetchall()
    
    print(f"Total books loaded: {len(books)}")
    
    seen_hashes = {}
    to_update = []
    
    for bid, title, author, cat, cover in books:
        fname = os.path.basename(cover)
        fpath = os.path.join(IMG_DIR, fname)
        h = get_file_hash(fpath)
        sz = os.path.getsize(fpath) if os.path.exists(fpath) else 0
        
        is_syn = is_synthetic_cover(fpath)
        known_bad = fname in ['tu_tuong_hcm.jpg', 'doi_gio_hu.jpg', 'canh_dong_hoang.jpg', 
                              'toan_roi_rac.jpg', 'so_do.jpg', 'atomic_habits.jpg', 'clean_code.jpg']
        
        if is_syn or (h in seen_hashes) or (sz < 15500) or known_bad:
            to_update.append((bid, title, author, cat, fname, fpath))
        else:
            seen_hashes[h] = (bid, title)
            
    print(f"Books with good authentic covers: {len(seen_hashes)}")
    print(f"Books needing authentic web covers: {len(to_update)}")
    
    updated_count = 0
    failed_list = []
    
    for i, (bid, title, author, cat, fname, fpath) in enumerate(to_update):
        print(f"[{i+1}/{len(to_update)}] Searching web for {bid}: {title} ({author})...")
        raw, url, new_hash = fetch_real_cover_for_book(title, author, seen_hashes)
        
        if raw and new_hash:
            with open(fpath, "wb") as f:
                f.write(raw)
            target_fpath = os.path.join(TARGET_IMG_DIR, fname)
            with open(target_fpath, "wb") as f:
                f.write(raw)
                
            seen_hashes[new_hash] = (bid, title)
            updated_count += 1
            print(f"   -> [SUCCESS] Downloaded authentic cover ({new_hash[:8]} - {len(raw)} bytes) from:\n      {url[:90]}")
        else:
            print(f"   -> [FAILED] Could not find authentic web cover for {title}")
            failed_list.append((bid, title, author))
            
    print("\n===============================")
    print(f"Completed: {updated_count}/{len(to_update)} updated.")
    if failed_list:
        print(f"Failed count: {len(failed_list)}")
        for f in failed_list:
            print(f"  - {f[0]}: {f[1]} ({f[2]})")
    print("===============================\n")

if __name__ == "__main__":
    main()
