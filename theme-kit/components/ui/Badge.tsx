import { cn } from "@/lib/utils";
import { ComponentProps } from "react";

type Tone = "default" | "primary" | "success" | "warning" | "muted";

const tones: Record<Tone, string> = {
  default: "bg-foreground/5 text-foreground",
  primary: "bg-primary/10 text-primary",
  success: "bg-success/10 text-success",
  warning: "bg-warning/15 text-accent-foreground",
  muted: "bg-muted text-muted-foreground",
};

export function Badge({
  tone = "default",
  className,
  ...props
}: { tone?: Tone } & ComponentProps<"span">) {
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold",
        tones[tone],
        className,
      )}
      {...props}
    />
  );
}
