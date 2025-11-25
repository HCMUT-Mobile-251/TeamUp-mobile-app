import pandas as pd
import json

# Đọc file Excel
df = pd.read_excel('Các môn học trường bk.xlsx')

# Xóa cột STT và BB/TC
df = df.drop(['STT', 'Số tín chỉ', 'BB/TC'], axis=1)

# Chuyển đổi sang JSON
json_data = df.to_json(orient='records', force_ascii=False, indent=2)

print(json_data)

# Lưu thành file JSON (tuỳ chọn)
with open('mon_hoc.json', 'w', encoding='utf-8') as f:
    f.write(json_data)