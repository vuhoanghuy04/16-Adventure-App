import json
import urllib.parse
import urllib.request
import time

# Đọc file dữ liệu cũ của bạn
with open('monuments.json', 'r', encoding='utf-8') as f:
    monuments = json.load(f)

print("Đang quét tự động tọa độ 57 địa điểm trên Bản đồ. Vui lòng đợi...")

for m in monuments:
    # Cấu trúc câu truy vấn tìm kiếm
    query = f"{m['name']}, Hải Phòng, Việt Nam"
    url = "https://nominatim.openstreetmap.org/search?q=" + urllib.parse.quote(query) + "&format=json&limit=1"
    
    try:
        req = urllib.request.Request(url, headers={'User-Agent': '16-Adventure-App/1.0'})
        response = urllib.request.urlopen(req)
        data = json.loads(response.read().decode('utf-8'))
        
        # Nếu tìm thấy trên bản đồ, cập nhật lại tọa độ
        if len(data) > 0:
            m['lat'] = round(float(data[0]['lat']), 6)
            m['lng'] = round(float(data[0]['lon']), 6)
            print(f"✅ Đã fix chuẩn: {m['name']} -> ({m['lat']}, {m['lng']})")
        else:
            print(f"⚠️ Không tìm thấy: {m['name']} (Giữ nguyên tọa độ cũ)")
    except Exception as e:
        print(f"❌ Lỗi mạng: {m['name']} - {e}")
    
    # Nghỉ 1 giây để không bị API chặn (Rate Limit)
    time.sleep(1)

# Lưu lại toàn bộ vào một file mới
with open('monuments_fixed.json', 'w', encoding='utf-8') as f:
    json.dump(monuments, f, ensure_ascii=False, indent=2)

print("\n🎉 HOÀN TẤT! File mới đã được lưu thành 'monuments_fixed.json'.")
