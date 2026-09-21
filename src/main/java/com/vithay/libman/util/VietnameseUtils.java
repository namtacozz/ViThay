package com.vithay.libman.util;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tiện ích chuẩn hóa và so khớp chuỗi Tiếng Việt thông minh.
 * Hỗ trợ tìm kiếm không phân biệt chữ hoa chữ thường,
 * khớp chính xác cả khi người dùng gõ có dấu lẫn không dấu (Telex/VNI)
 * và tự động giải mã các tổ hợp phím Telex thô (như Xuws -> Xứ, Toans -> Toán).
 */
public class VietnameseUtils {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final Map<Character, String> TONE_A = Map.of('s', "á", 'f', "à", 'r', "ả", 'x', "ã", 'j', "ạ");
    private static final Map<Character, String> TONE_AW = Map.of('s', "ắ", 'f', "ằ", 'r', "ẳ", 'x', "ẵ", 'j', "ặ");
    private static final Map<Character, String> TONE_AA = Map.of('s', "ấ", 'f', "ầ", 'r', "ẩ", 'x', "ẫ", 'j', "ậ");
    private static final Map<Character, String> TONE_E = Map.of('s', "é", 'f', "è", 'r', "ẻ", 'x', "ẽ", 'j', "ẹ");
    private static final Map<Character, String> TONE_EE = Map.of('s', "ế", 'f', "ề", 'r', "ể", 'x', "ễ", 'j', "ệ");
    private static final Map<Character, String> TONE_I = Map.of('s', "í", 'f', "ì", 'r', "ỉ", 'x', "ĩ", 'j', "ị");
    private static final Map<Character, String> TONE_O = Map.of('s', "ó", 'f', "ò", 'r', "ỏ", 'x', "õ", 'j', "ọ");
    private static final Map<Character, String> TONE_OO = Map.of('s', "ố", 'f', "ồ", 'r', "ổ", 'x', "ỗ", 'j', "ộ");
    private static final Map<Character, String> TONE_OW = Map.of('s', "ớ", 'f', "ờ", 'r', "ở", 'x', "ỡ", 'j', "ợ");
    private static final Map<Character, String> TONE_U = Map.of('s', "ú", 'f', "ù", 'r', "ủ", 'x', "ũ", 'j', "ụ");
    private static final Map<Character, String> TONE_UW = Map.of('s', "ứ", 'f', "ừ", 'r', "ử", 'x', "ữ", 'j', "ự");
    private static final Map<Character, String> TONE_Y = Map.of('s', "ý", 'f', "ỳ", 'r', "ỷ", 'x', "ỹ", 'j', "ỵ");

    /**
     * Loại bỏ toàn bộ dấu thanh và dấu mũ tiếng Việt.
     * Ví dụ: "Tư Tưởng Hồ Chí Minh" -> "Tu Tuong Ho Chi Minh", "Đắc Nhân Tâm" -> "Dac Nhan Tam"
     */
    public static String removeAccents(String str) {
        if (str == null) {
            return "";
        }
        String nfd = Normalizer.normalize(str, Normalizer.Form.NFD);
        String noAccents = DIACRITICS_PATTERN.matcher(nfd).replaceAll("");
        return noAccents.replace('đ', 'd')
                        .replace('Đ', 'D')
                        .replace('₫', 'd');
    }

    /**
     * Giải mã chuỗi Telex thô sang Tiếng Việt có dấu.
     * Ví dụ:
     * - "xuws" -> "xứ"
     * - "toans" -> "toán"
     * - "ddawsc" -> "đắc"
     * - "tieeur thuyeets" -> "tiểu thuyết"
     */
    public static String decodeTelex(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(decodeTelexWord(words[i]));
        }
        return sb.toString();
    }

    private static String decodeTelexWord(String word) {
        if (word == null || word.length() < 2) return word;
        String w = word.toLowerCase();

        // 1. Phụ âm đ
        w = w.replace("dd", "đ");

        // 2. Nguyên âm kép – thứ tự quan trọng: xử lý tổ hợp trước, đơn sau
        w = w.replace("aw", "ă")
             .replace("aa", "â")
             .replace("ee", "ê")
             .replace("oo", "ô")
             .replace("ow", "ơ")
             .replace("uw", "ư");

        // 'w' đơn lẻ sau u hoặc o (edge-case composition)
        w = w.replaceAll("u\\+?w", "ư").replaceAll("o\\+?w", "ơ");

        // 'w' đơn lẻ hoàn toàn = 'ư' (Telex chuẩn: bấm w không kèm nguyên âm)
        // Chỉ áp dụng khi 'w' còn lại sau tất cả các thay thế trên
        w = w.replace("w", "ư");

        // 3. Tìm dấu thanh (s, f, r, x, j)
        char tone = 0;
        int toneIdx = -1;
        for (int i = w.length() - 1; i >= 1; i--) {
            char c = w.charAt(i);
            if (c == 's' || c == 'f' || c == 'r' || c == 'x' || c == 'j') {
                tone = c;
                toneIdx = i;
                break;
            }
        }

        if (tone != 0 && toneIdx != -1) {
            String base = w.substring(0, toneIdx) + w.substring(toneIdx + 1);
            // Tìm nguyên âm để đặt dấu
            char[] vowels = {'ă', 'â', 'ê', 'ô', 'ơ', 'ư', 'a', 'e', 'o', 'u', 'i', 'y'};
            for (char v : vowels) {
                int idx = base.lastIndexOf(v);
                if (idx != -1) {
                    String replacement = getTonedVowel(v, tone);
                    if (replacement != null) {
                        base = base.substring(0, idx) + replacement + base.substring(idx + 1);
                        return base;
                    }
                }
            }
        }

        return w;
    }

    private static String getTonedVowel(char v, char tone) {
        switch (v) {
            case 'a': return TONE_A.get(tone);
            case 'ă': return TONE_AW.get(tone);
            case 'â': return TONE_AA.get(tone);
            case 'e': return TONE_E.get(tone);
            case 'ê': return TONE_EE.get(tone);
            case 'i': return TONE_I.get(tone);
            case 'o': return TONE_O.get(tone);
            case 'ô': return TONE_OO.get(tone);
            case 'ơ': return TONE_OW.get(tone);
            case 'u': return TONE_U.get(tone);
            case 'ư': return TONE_UW.get(tone);
            case 'y': return TONE_Y.get(tone);
            default: return null;
        }
    }

    /**
     * Kiểm tra chuỗi nguồn có chứa từ khóa tìm kiếm hay không.
     * So sánh linh hoạt:
     * 1. So khớp trực tiếp (case-insensitive có dấu).
     * 2. So khớp sau khi loại bỏ dấu (accent-insensitive).
     * 3. So khớp sau khi giải mã Telex (Ví dụ: 'xuws' khớp 'Xứ Cát').
     */
    public static boolean matches(String source, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        if (source == null || source.trim().isEmpty()) {
            return false;
        }

        String sLower = source.toLowerCase();
        String qLower = query.toLowerCase().trim();

        // 1. Khớp trực tiếp có dấu
        if (sLower.contains(qLower)) {
            return true;
        }

        // 2. Khớp không dấu
        String sNormalized = removeAccents(sLower);
        String qNormalized = removeAccents(qLower);
        if (sNormalized.contains(qNormalized)) {
            return true;
        }

        // 3. Khớp sau khi giải mã Telex
        String qTelex = decodeTelex(qLower);
        if (!qTelex.equals(qLower)) {
            if (sLower.contains(qTelex)) {
                return true;
            }
            if (sNormalized.contains(removeAccents(qTelex))) {
                return true;
            }
        }

        return false;
    }
}
