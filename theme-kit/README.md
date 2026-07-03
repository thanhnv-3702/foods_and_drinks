# TechJA Theme Kit

Bộ theme/style tách riêng từ web TechJA (LMS) để mang sang dự án khác. Phong cách **Untitled UI**, màu chủ đạo **cam `#f38744`**, font **Inter**, dùng **Tailwind CSS v4** + design tokens dạng CSS variables.

Folder này tự chứa đủ để một AI/agent (hoặc bạn) phân tích và áp dụng vào web mới.

---

## 1. Cấu trúc folder

```
techja-theme-kit/
├─ README.md                      # File này: phân tích + hướng dẫn áp dụng
├─ design-tokens.json             # Toàn bộ token dạng JSON (tool-agnostic, dễ phân tích)
├─ styles/
│  ├─ tokens.css                  # CSS variables thuần (:root) — dùng được với mọi stack
│  ├─ theme.tailwind-v4.css       # Theme cho Tailwind v4 (@theme inline + utilities)
│  └─ tailwind.config.v3.js       # Config cho Tailwind v3
├─ lib/
│  └─ utils.ts                    # Hàm cn() gộp class (clsx + tailwind-merge)
├─ components/ui/
│  ├─ Button.tsx                  # Button + ButtonLink (4 variant, 3 size)
│  ├─ Badge.tsx                   # Badge (5 tone)
│  ├─ SectionHeading.tsx          # Tiêu đề khu vực + link "Xem tất cả"
│  └─ Rating.tsx                  # Đánh giá sao
└─ assets/
   ├─ techja-icon.svg             # Icon logo (2 hình thoi gradient cam)
   ├─ favicon-icon.svg            # Bản dùng cho favicon (app/icon.svg)
   └─ techja-logo.png             # Logo ngang đầy đủ
```

---

## 2. Design tokens

Toàn bộ màu là **semantic token** (đặt theo vai trò, không theo màu cụ thể), nên đổi theme = đổi giá trị ở `:root`.

| Token                  | Giá trị     | Vai trò                                  |
| ---------------------- | ----------- | ---------------------------------------- |
| `--background`         | `#ffffff`   | Nền trang                                |
| `--foreground`         | `#101828`   | Chữ chính                                |
| `--card`               | `#ffffff`   | Nền thẻ/panel                            |
| `--primary`            | `#f38744`   | Màu thương hiệu (cam) — nút, nhấn mạnh   |
| `--primary-foreground` | `#ffffff`   | Chữ trên nền primary                     |
| `--secondary`          | `#2e90fa`   | Màu phụ (xanh dương)                     |
| `--accent`             | `#fef6ee`   | Nền nhấn nhẹ (cam rất nhạt)              |
| `--accent-foreground`  | `#b93815`   | Chữ trên nền accent / số liệu nổi bật    |
| `--muted`              | `#f2f4f7`   | Nền phụ, trạng thái mờ                    |
| `--muted-foreground`   | `#667085`   | Chữ phụ                                   |
| `--border`             | `#eaecf0`   | Viền                                      |
| `--input`              | `#d0d5dd`   | Viền input                                |
| `--ring`               | `#f38744`   | Vòng focus                                |
| `--success`            | `#15803d`   | Thành công                                |
| `--warning`            | `#f79009`   | Cảnh báo / sao đánh giá                   |
| `--danger`             | `#f04438`   | Lỗi / nguy hiểm                           |
| `--radius`             | `0.5rem`    | Bo góc gốc (sm/md/lg/xl suy ra từ đây)   |

**Quy ước font:** Inter, các weight 300–800. `--font-sans` map vào Tailwind `font-sans`.

**Bo góc đặc trưng:** nút và badge dùng `rounded-full` (bo tròn hoàn toàn) — đây là dấu hiệu nhận diện của style này.

---

## 3. Cách áp dụng

### A. Dự án Tailwind v4 (giống dự án gốc — khuyến nghị)

1. Copy `styles/theme.tailwind-v4.css`, `lib/utils.ts`, `components/`, `assets/` vào dự án.
2. Ở file CSS entry (vd `app/globals.css`):

```css
@import "tailwindcss";
@import "./theme.tailwind-v4.css";
```

3. Nạp font Inter (Next.js):

```tsx
import { Inter } from "next/font/google";
const inter = Inter({ variable: "--font-sans", subsets: ["latin", "vietnamese"], weight: ["300","400","500","600","700","800"] });
// <html className={inter.variable}>
```

4. Cài deps cho components: `npm i clsx tailwind-merge lucide-react`.

### B. Dự án Tailwind v3

1. Copy `styles/tokens.css` vào và import ở entry CSS (hoặc dán khối `:root` vào globals).
2. Merge `styles/tailwind.config.v3.js` vào `tailwind.config.js` của bạn.
3. Cài deps như trên.

### C. Dự án KHÔNG dùng Tailwind

- Dùng `styles/tokens.css` làm nguồn biến CSS, rồi viết CSS thường tham chiếu `var(--primary)`, `var(--radius)`...
- `design-tokens.json` dùng để feed vào Style Dictionary / Figma Tokens / hoặc cho AI phân tích.
- Lưu ý: các component `.tsx` viết bằng class Tailwind nên cần map lại sang CSS của bạn.

---

## 4. Lưu ý khi port components

- Components dùng alias `@/lib/utils`. Hãy đảm bảo dự án có alias `@/*` trỏ tới `src/` (hoặc đổi sang đường dẫn tương đối).
- `Button.tsx` và `SectionHeading.tsx` import `next/link` (Next.js). Nếu không dùng Next, thay `Link` bằng thẻ `<a>` hoặc router của bạn.
- Icon dùng `lucide-react`.
- Class `container-page`, `line-clamp-2/3`, `no-scrollbar` đến từ `theme.tailwind-v4.css` (hoặc copy từ đó).

---

## 5. Bảng tham chiếu component nhanh

- **Button**: `variant = primary | secondary | outline | ghost`, `size = sm | md | lg`. Bo `rounded-full`, có ring focus.
- **Badge**: `tone = default | primary | success | warning | muted`. Pill nhỏ, chữ semibold.
- **SectionHeading**: `title`, `subtitle?`, `href?`, `linkLabel?` — tiêu đề khu vực kèm link mũi tên.
- **Rating**: `value`, `count?`, `size?`, `showValue?` — sao vàng (`--warning`), số điểm màu `accent-foreground`.

---

## 6. Prompt gợi ý để nhờ AI áp dụng ở web khác

> "Đây là `techja-theme-kit`. Hãy đọc `README.md` + `design-tokens.json`, rồi áp dụng theme này vào dự án hiện tại: nạp tokens, cấu hình Tailwind tương ứng phiên bản đang dùng, thêm font Inter, và refactor các nút/thẻ/heading hiện có sang dùng token + component trong kit. Giữ nguyên semantic token, không hardcode mã màu."
