package com.duolingo.app.utils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.regex.Pattern;

public class MockAiEngine {

    private static HashMap<String, String[]> dictionary = null;

    private static void initDictionary(Context context) {
        if (dictionary != null) return;
        dictionary = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("english_lessons.csv")));
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                // Split by comma, respecting quotes
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 4) {
                    String word = parts[0].replace("\"", "").trim().toLowerCase();
                    dictionary.put(word, parts);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tiện ích xóa dấu Tiếng Việt cực đỉnh để đối phó lỗi bàn phím Emulator / ADB
    private static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    private static String formatDictionaryResponse(String[] parts) {
        String wordFound = parts[0].replace("\"", "");
        String definition = parts[1].replace("\"", "");
        String vietnamese = parts[2].replace("\"", "");
        String example = parts[3].replace("\"", "");
        String pos = parts.length >= 5 ? parts[4].replace("\"", "") : "";

        return "📖 **'" + wordFound + "'** (" + pos + "): **" + vietnamese + "**\n\n" +
               "📝 **Giải nghĩa:** " + definition + "\n" +
               "💡 **Ví dụ:** *" + example + "*";
    }

    public static String getResponse(Context context, String query) {
        String q = query.toLowerCase();
        String qNoAccent = removeAccent(q); // Lấy bản KHÔNG DẤU để so sánh

        if (context != null) {
            initDictionary(context);
        }

        // ============================================
        // 1. CHÀO HỎI & GIAO TIẾP CƠ BẢN
        // ============================================
        if (q.equals("hi") || q.equals("hello") || qNoAccent.equals("chao") || qNoAccent.contains("xin chao")) {
            return "👋 Chào bạn! Mình là DuDu - Trợ lý siêu cấp thông minh đây. Bạn cứ thả câu hỏi (kể cả gõ không dấu nha) vào đây nhé!";
        } 
        else if (qNoAccent.contains("ban la ai") || q.contains("who are you")) {
            return "🤖 Mình là **DuDu**, siêu trợ lý AI được tích hợp trực tiếp trong VocaVerse!\nNhiệm vụ của mình là giúp bạn chinh phục đỉnh cao Tiếng Anh dễ dàng nhất.";
        }
        else if (qNoAccent.contains("cam on") || q.contains("thank you") || q.contains("thanks")) {
            return "🥰 Không có gì đâu! Mình luôn ở đây để giúp đỡ bạn. Càng hỏi nhiều, mình càng thích!";
        }
        else if (qNoAccent.contains("nhu the nao") && qNoAccent.contains("hom nay")) {
             return "🌟 Hôm nay là một ngày tuyệt vời để học tiếng Anh! Bạn đã sẵn sàng nạp thêm 10 từ vựng xuất sắc chưa?";
        }

        // ============================================
        // 2. IDIOMS (THÀNH NGỮ) SIÊU ĐA DẠNG
        // ============================================
        else if (q.contains("piece of cake") || qNoAccent.contains("de nhu an keo")) {
            return "🍰 **\"A piece of cake\"**: Một việc gì đó rất dễ dàng.\nVí dụ: *The math test was a piece of cake.*";
        }
        else if (q.contains("break a leg")) {
            return "🎭 **\"Break a leg\"**: Chúc may mắn (thường dùng trước khi lên sân khấu hoặc thi cử).\nVí dụ: *You have an exam tomorrow? Break a leg!*";
        }
        else if (q.contains("bite the bullet")) {
            return "🦷 **\"Bite the bullet\"**: Cố gắng nhẫn nhịn chịu đựng một việc khó khăn không thể tránh khỏi.\nVí dụ: *I'll just have to bite the bullet.*";
        }
        else if (q.contains("under the weather")) {
            return "🤒 **\"Under the weather\"**: Cảm thấy không khỏe, bị ốm nhẹ.\nVí dụ: *I'm feeling a bit under the weather today.*";
        }
        else if (q.contains("spill the beans")) {
            return "🫘 **\"Spill the beans\"**: Vô tình tiết lộ một bí mật.\nVí dụ: *Come on, spill the beans! What did he say?*";
        }
        else if (q.contains("hit the hay") || q.contains("hit the sack")) {
            return "🛌 **\"Hit the hay\" / \"Hit the sack\"**: Đi ngủ.\nVí dụ: *It's been a long day, I'm going to hit the hay.*";
        }
        else if (q.contains("cost an arm and a leg")) {
             return "💰 **\"Cost an arm and a leg\"**: Có giá rất mắc, cực kỳ đắt đỏ.\nVí dụ: *This sports car costs an arm and a leg!*";
        }

        // ============================================
        // 3. PHRASAL VERBS (CỤM ĐỘNG TỪ CHUYÊN SÂU)
        // ============================================
        else if ((qNoAccent.contains("cum dong tu") || q.contains("phrasal verb")) && !q.contains("look")) {
            return "🔗 Phrasal Verbs là linh hồn của giao tiếp! Bạn hãy hỏi mình về:\n1. `Look after`\n2. `Give up`\n3. `Put off`\n4. `Take off`";
        }
        else if (q.contains("look after")) {
             return "👀 **Look after**: Chăm sóc (ai đó/con vật nào đó).\nVí dụ: *Can you look after my dog while I'm on holiday?*";
        }
        else if (q.contains("give up") || qNoAccent.contains("tu bo")) {
             return "🏳️ **Give up**: Từ bỏ, bỏ cuộc.\nVí dụ: *Never give up on your dreams!* (Đừng bao giờ từ bỏ ước mơ!).";
        }
        else if (q.contains("put off") || qNoAccent.contains("tri hoan")) {
             return "⏳ **Put off**: Trì hoãn một việc gì đó.\nVí dụ: *They had to put off the meeting until tomorrow.*";
        }
        else if (q.contains("take off")) {
            return "🛫 **Take off** có 2 nghĩa phổ biến:\n1. Cất cánh (máy bay): *The plane took off on time.*\n2. Cởi (quần áo/giày): *Please take off your shoes.*";
       }

        // ============================================
        // 4. NGỮ PHÁP (GRAMMAR) SIÊU RỘNG
        // ============================================
        else if (qNoAccent.contains("hien tai don") || q.contains("present simple")) {
            return "📚 **Thì hiện tại đơn (Present Simple)** dùng để diễn tả sự thật hiển nhiên hoặc thói quen.\n- Cấu trúc: `S + V(s/es)`\n- Ví dụ: *I play football every day.*";
        }
        else if (qNoAccent.contains("hien tai tiep dien") || q.contains("present continuous")) {
            return "⏳ **Thì hiện tại tiếp diễn (Present Continuous)** dùng để diễn tả hành động đang diễn ra NGAY LÚC NÀY.\n- Cấu trúc: `S + am/is/are + V_ing`\n- Ví dụ: *I am studying English now.*";
        }
        else if (qNoAccent.contains("hien tai hoan thanh") || q.contains("present perfect")) {
            return "🎯 **Thì hiện tại hoàn thành (Present Perfect)** diễn tả hành động bắt đầu ở quá khứ và kéo dài đến hiện tại.\n- Cấu trúc: `S + have/has + V3/ed`\n- Ví dụ: *I have lived in Hanoi for 5 years.*";
        }
        else if (qNoAccent.contains("qua khu don") || q.contains("past simple")) {
            return "🕰️ **Thì quá khứ đơn (Past Simple)** diễn tả hành động đã kết thúc hoàn toàn trong quá khứ.\n- Cấu trúc: `S + V_ed / V_cột 2`\n- Ví dụ: *I visited London last year.*";
        }
        else if (qNoAccent.contains("tuong lai don") || q.contains("future simple")) {
            return "🚀 **Thì tương lai đơn (Future Simple)** diễn tả hành động sẽ xảy ra, hoặc một dự đoán.\n- Cấu trúc: `S + will + V`\n- Ví dụ: *It will rain tomorrow.*";
        }
        else if (qNoAccent.contains("cau dieu kien loai 1") || qNoAccent.contains("dieu kien 1")) {
            return "🌧️ **Câu điều kiện loại 1** (Có thể xảy ra ở hiện tại/tương lai).\n- Cấu trúc: `If + S + V(hiện tại), S + will + V`\n- Ví dụ: *If it rains, we will stay at home.*";
        }
        else if (qNoAccent.contains("cau dieu kien loai 2") || qNoAccent.contains("dieu kien 2")) {
            return "💭 **Câu điều kiện loại 2** (Trái với thực tại).\n- Cấu trúc: `If + S + V(quá khứ đơn), S + would + V`\n- Ví dụ: *If I were a bird, I would fly.*";
        }
        else if (qNoAccent.contains("menh de quan he") || q.contains("relative clause")) {
            return "🔗 **Mệnh đề quan hệ (Relative Clauses)** dùng để bổ nghĩa cho danh từ đứng trước nó.\n- Cấu trúc dùng `who` (cho người), `which` (cho vật), `that` (đa năng).\n- Ví dụ: *The boy **who** is singing is my brother.*";
        }
        else if (qNoAccent.contains("cau bi dong") || q.contains("passive voice")) {
            return "🔄 **Câu bị động (Passive Voice)**: Nhấn mạnh vào đối tượng bị tác động.\n- Cấu trúc: `S + to be + V3/ed + (by O)`\n- Ví dụ: *The window was broken by Peter.*";
        }
        else if (qNoAccent.contains("so sanh hon") || q.contains("comparative")) {
             return "⚖️ **So sánh hơn (Comparative)**:\n- Tính từ ngắn: `S + to be + Adj_er + than + N/Pronoun`\n- Tính từ dài: `S + to be + more + Adj + than + N`";
        }

        // ============================================
        // 5. TỪ VỰNG THEO CHUYÊN ĐỀ ĐA DẠNG
        // ============================================
        else if (qNoAccent.contains("gia dinh") || q.contains("family")) {
            return "👨‍👩‍👧‍👦 **Từ vựng chủ đề Gia đình:**\n1. Parents (Bố mẹ)\n2. Sibling (Anh chị em ruột)\n3. Relatives (Họ hàng)\n4. Nephew / Niece (Cháu trai / cháu gái)";
        }
        else if (qNoAccent.contains("dong vat") || q.contains("animal")) {
            return "🦁 **Từ vựng Động vật:**\n1. Elephant (Voi)\n2. Giraffe (Hươu cao cổ)\n3. Dolphin (Cá heo)\n4. Cheetah (Báo đốm)";
        }
        else if (qNoAccent.contains("du lich") || q.contains("travel")) {
            return "✈️ **Từ vựng Du lịch:**\n1. Destination (Điểm đến)\n2. Luggage (Hành lý)\n3. Boarding pass (Thẻ lên máy bay)\n4. Itinerary (Lịch trình)";
        }
        else if (qNoAccent.contains("thoi tiet") || q.contains("weather")) {
             return "🌤️ **Từ vựng Thời tiết:**\n1. Chilly (Lạnh buốt)\n2. Breezy (Gió nhẹ)\n3. Drizzle (Mưa phùn)\n4. Scorching (Nóng rát)";
        }

        // ============================================
        // 6. GIAO TIẾP THỰC TẾ & TỪ LÓNG (SLANG)
        // ============================================
        else if (qNoAccent.contains("xin loi") || q.contains("apologize")) {
            return "🙏 **Các cách nói Xin lỗi trong tiếng Anh:**\n1. **I'm sorry.**\n2. **My apologies.** (Trang trọng)\n3. **My bad.** (Lỗi của tôi - dùng với bạn bè)\n4. **I didn't mean to...** (Tôi không cố ý...)";
        }
        else if (qNoAccent.contains("hoi duong") || qNoAccent.contains("duong di")) {
            return "🗺️ **Mẫu câu hỏi đường:**\n- *Excuse me, could you tell me how to get to the hospital?*\n- *Is there a bank near here?*";
        }
        else if (qNoAccent.contains("tu long") || q.contains("slang")) {
            return "😎 **Từ lóng (Slang) cực ngầu của gen Z:**\n1. **Gonna** (Going to): Sắp sửa\n2. **Wanna** (Want to): Muốn\n3. **Lemme** (Let me): Để tôi\n4. **Gotta** (Got to): Phải làm gì đó";
        }
        else if (qNoAccent.contains("phong van") || qNoAccent.contains("gioi thieu ban than")) {
            return "👔 **Form giới thiệu bản thân ăn điểm:**\n- *Hi, my name is [Name]. I graduated from [University] with a degree in [Major]...*";
        }

        // ============================================
        // 7. PHÂN BIỆT TỪ DỄ NHẦM LẪN
        // ============================================
        else if (qNoAccent.contains("phan biet") && q.contains("make") && q.contains("do")) {
             return "⚠️ **Phân biệt Make và Do:**\n- **Make** dùng khi bạn *tạo ra, sản xuất* thứ gì đó mới. (Ví dụ: Make a cake).\n- **Do** dùng khi thực hiện một *hành động, nhiệm vụ* (Ví dụ: Do homework).";
        }
        else if ((qNoAccent.contains("phan biet") || qNoAccent.contains("khac nhau")) && q.contains("see") && q.contains("look")) {
             return "👁️ **See vs Look vs Watch:**\n- **See**: Thấy một cách thụ động.\n- **Look**: Chủ động nhìn vào cái gì đó.\n- **Watch**: Nhìn chằm chằm và theo dõi một diễn biến (ví dụ: Watch TV).";
        }

        // ============================================
        // 8. DỊCH THUẬT SIÊU NHANH TỪ CƠ SỞ DỮ LIỆU CSV (Assets) (HỖ TRỢ LẬP TRÌNH KHÔNG DẤU)
        // ============================================
        else if (qNoAccent.contains("dich") || qNoAccent.contains("nghia la gi") || 
                 q.contains("translate") || qNoAccent.contains("tieng viet") || qNoAccent.contains("la gi")) {
            
            if (dictionary != null && !dictionary.isEmpty()) {
                // Xóa cả tiếng việt có dấu và không dấu
                String cleaned = qNoAccent.replace("dich", "").replace("tu", "").replace("nghia", "")
                                  .replace("la", "").replace("gi", "").replace("chu", "")
                                  .replace("tieng", "").replace("viet", "").replace("giup", "")
                                  .replace("minh", "").replace("duoc", "").replace("khong", "")
                                  .replace("ban", "").replace("hay", "").replace("cho", "").trim();
                cleaned = cleaned.replaceAll("[\\?\\.\\!\\\"\\'\\“\\”]", "").trim();
                
                // Tiếng Anh luôn không dấu, nên có thể tìm trực tiếp 'cleaned' 
                if (!cleaned.isEmpty() && dictionary.containsKey(cleaned)) {
                    return formatDictionaryResponse(dictionary.get(cleaned));
                }

                // Nếu không được, cắt mảng và tìm từng từ
                String[] wordsInQuery = cleaned.split("[\\s\\?\\.\\!\\\"\\'\\“\\”]+");
                for (String w : wordsInQuery) {
                    if (w.length() < 2) continue;
                    if (w.equals("dich") || w.equals("tu") || w.equals("nghia") || w.equals("la") || 
                        w.equals("gi") || w.equals("tieng") || w.equals("viet") || w.equals("ban") || 
                        w.equals("co") || w.equals("the") || w.equals("cho") || w.equals("minh") || 
                        w.equals("chu") || w.equals("nao") || w.equals("the") || w.equals("duoc") || 
                        w.equals("khong") || w.equals("va") || w.equals("voi")) continue;
                    
                    if (dictionary.containsKey(w)) {
                        return formatDictionaryResponse(dictionary.get(w));
                    }
                }
            }

            // Fallback nếu không có trong file CSV do gõ sai
            return "🤖 Từ vựng này hiện không có sẵn trong hệ thống Database " + (dictionary != null ? dictionary.size() : 0) + " từ của VocaVerse. Bạn thử tra các từ vựng kinh điển như: `abandon`, `accident`, `beautiful`, `capacity` xem tốc độ lấy dữ liệu của mình nhé!";
        } 
        
        // ============================================
        // MẶC ĐỊNH LƯỚI HỨNG RẤT NGHỆ THUẬT
        // ============================================
        return "Wow, câu hỏi này của bạn cực kỳ đẳng cấp! 😱\n\nSiêu máy tính ngôn ngữ cục bộ của mình đang chứa hơn " + (dictionary != null ? dictionary.size() : "3000") + " từ vựng thực tế và mẹo học. Để mình khoe cho bạn nhé, hãy gõ thử một trong số này (có dấu hay không dấu đều ăn hết!):\n" +
               "1. `Dich tu abandon`\n" +
               "2. `Phan biet make va do`\n" +
               "3. `Thi qua khu don`\n" +
               "4. `Menh de quan he`\n" +
               "5. `Tu vung thoi tiet`\n" +
               "6. `Cach gioi thieu ban than`\n\n" +
               "Hệ thống mình nay đã miễn nhiễm với mọi rào cản gõ lỗi phím rồi nha!";
    }
}
