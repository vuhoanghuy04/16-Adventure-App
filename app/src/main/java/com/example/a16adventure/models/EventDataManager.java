package com.example.a16adventure.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventDataManager {

    public static List<Event> getMockEvents() {
        List<Event> events = new ArrayList<>();

        events.add(new Event(
                "e1",
                "Lễ hội chọi trâu Đồ Sơn",
                "09/08/2026",
                "Đồ Sơn, Hải Phòng",
                "Lễ hội truyền thống đặc sắc của người dân vùng biển, thu hút đông đảo du khách tham quan mỗi năm.",
                "✔ Lễ hội có nguồn gốc từ tục thờ thủy thần của cư dân miền biển.\n✔ Thể hiện tinh thần thượng võ, lòng dũng cảm và sức mạnh của con người vùng biển.\n✔ Là dịp để cộng đồng gắn kết, bảo tồn và phát huy giá trị văn hóa truyền thống.",
                Arrays.asList("Rước thần linh ra sân đấu long trọng và trang nghiêm.", "Các trận chọi trâu sôi động, thu hút đông đảo người dân và du khách.", "Nghi thức trao giải và tế lễ tạ sau khi kết thúc hội."),
                "event_choitrau_doson",
                8
        ));

        events.add(new Event(
                "e2",
                "Lễ hội đua thuyền Cát Bà",
                "15/03/2026",
                "Cát Bà, Hải Phòng",
                "Lễ hội đua thuyền nhộn nhịp trên đảo Cát Bà với nhiều đội đua thuyền tham gia.",
                "✔ Tôn vinh tinh thần đoàn kết, vươn khơi bám biển.\n✔ Là dịp để ngư dân cầu mong một năm mưa thuận gió hòa, tôm cá đầy khoang.",
                Arrays.asList("Thi đua thuyền rồng truyền thống giữa các đội trên đảo.", "Biểu diễn văn nghệ và giao lưu ẩm thực hải sản.", "Trao giải và lễ cầu ngư."),
                "event_duathuyen_catba",
                3
        ));

        events.add(new Event(
                "e3",
                "Lễ hội Hoa Phượng Đỏ",
                "13/05/2026",
                "Trung tâm thành phố, Hải Phòng",
                "Sự kiện văn hóa du lịch thường niên đặc trưng lớn nhất của thành phố Hải Phòng.",
                "✔ Quảng bá hình ảnh thành phố Cảng với biểu tượng hoa phượng đỏ.\n✔ Thu hút đầu tư và phát triển du lịch địa phương.",
                Arrays.asList("Chương trình nghệ thuật khai mạc hoành tráng tại Nhà hát lớn.", "Lễ hội Carnival đường phố.", "Triển lãm ảnh và hội chợ thương mại quốc tế."),
                "event_hoaphuongdo",
                5
        ));

        events.add(new Event(
                "e4",
                "Lễ hội Đền Trạng Trình",
                "28/12/2026",
                "Vĩnh Bảo, Hải Phòng",
                "Tưởng nhớ Danh nhân văn hóa Nguyễn Bỉnh Khiêm, vị Trạng nguyên lỗi lạc.",
                "✔ Thể hiện truyền thống uống nước nhớ nguồn, tôn sư trọng đạo.\n✔ Khuyến khích tinh thần hiếu học của các thế hệ trẻ.",
                Arrays.asList("Lễ dâng hương Đại Tế tại đền chính.", "Tổ chức giải vật truyền thống và các trò chơi dân gian.", "Trình diễn Thư pháp và hát Khúc xướng."),
                "event_dentrangtrinh",
                12
        ));

        events.add(new Event(
                "e5",
                "Lễ hội Minh Thề",
                "08/02/2026",
                "Kiến Thụy, Hải Phòng",
                "Lễ hội độc đáo của người dân xưa, đặc trưng bởi lời thề trung thực, không tham nhũng.",
                "✔ Bắt nguồn từ thế kỷ 16 đời Mạc, thể hiện chí công vô tư.\n✔ Lời thề độc đáo răn dạy con người phải sống trong sạch, không trộm cắp, không tham nhũng.",
                Arrays.asList("Nghi thức tế thần linh và tổ chức uống rượu máu ăn thề.", "Các trò chơi dân gian như đấu vật, thổi cơm thi.", "Diễu hành quanh đền thờ trong không khí trang nghiêm."),
                "event_minhthe",
                2 // Tháng 2 Dương lịch (Khoảng tháng Giêng nâm lịch)
        ));

        events.add(new Event(
                "e6",
                "Lễ hội Núi Voi",
                "26/02/2026",
                "An Lão, Hải Phòng",
                "Lễ hội lớn đầu xuân, vinh danh nữ tướng Lê Chân và nét văn hóa vùng đồng bằng Bắc Bộ.",
                "✔ Tri ân nữ tướng anh hùng Lê Chân đã có công khai hoang, đánh giặc.\n✔ Khởi dậy lòng tự hào về mảnh đất An Lão oai hùng.",
                Arrays.asList("Lễ rước kiệu hoành tráng từ chân lên đỉnh núi Voi.", "Hội thi hát chèo, hát đúm và giao lưu quan họ.", "Tổ chức múa rối nước và cờ tướng ngoài trời."),
                "event_nuivoi",
                2
        ));

        events.add(new Event(
                "e7",
                "Lễ hội đánh Pháo Đất",
                "15/09/2026",
                "Vĩnh Bảo, Hải Phòng",
                "Trò chơi dân gian đặc trưng tưng bừng pháo đất, mong mưa thuận gió hòa.",
                "✔ Bắt nguồn từ truyền thuyết từ thời Hai Bà Trưng đánh giặc.\n✔ Cầu mong thời tiết êm đềm, mùa màng bội thu.",
                Arrays.asList("Thi làm pháo từ đất sét đặc biệt.", "Trình diễn gieo pháo, tiếng nổ vang rền thể hiện sức mạnh.", "Giao lưu múa lân sư rồng quanh làng."),
                "event_phaodat",
                9
        ));

        events.add(new Event(
                "e8",
                "Hội đua thuyền rồng trên biển",
                "28/01/2026",
                "Đồ Sơn, Hải Phòng",
                "Mở đầu cho năm mới của người dân đi biển Đồ Sơn với ý chí chinh phục biển khơi.",
                "✔ Ra mắt dịp đầu năm để tế Mẫu, tế Thủy thần, mong tôm cá đầy khoang.\n✔ Thể hiện kỹ năng điêu luyện và tinh thần thép của trai tráng miền biển.",
                Arrays.asList("Thi đua thuyền rồng giữa các phường nghề cá.", "Lễ cúng cầu ngư tại bến cảng.", "Liên hoan ẩm thực món ngon từ hải sản tươi sống."),
                "event_duathuyen_bien",
                1
        ));

        return events;
    }
}
