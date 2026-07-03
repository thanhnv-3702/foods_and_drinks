import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

/** Gop class Tailwind an toan (xu ly trung lap). Yeu cau: clsx, tailwind-merge */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
