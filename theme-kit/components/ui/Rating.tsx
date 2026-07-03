import { Star } from "lucide-react";
import { cn } from "@/lib/utils";

export function Rating({
  value,
  count,
  size = 14,
  showValue = true,
}: {
  value: number;
  count?: number;
  size?: number;
  showValue?: boolean;
}) {
  return (
    <div className="flex items-center gap-1">
      {showValue && (
        <span className="text-sm font-bold text-accent-foreground">
          {value.toFixed(1)}
        </span>
      )}
      <div className="flex">
        {Array.from({ length: 5 }).map((_, i) => {
          const isFilled = i + 1 <= Math.round(value);
          return (
            <Star
              key={i}
              size={size}
              className={cn(
                isFilled ? "fill-warning text-warning" : "fill-muted text-muted",
              )}
            />
          );
        })}
      </div>
      {count !== undefined && (
        <span className="text-xs text-muted-foreground">({count})</span>
      )}
    </div>
  );
}
