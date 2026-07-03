import Link from "next/link";
import { ArrowRight } from "lucide-react";

export function SectionHeading({
  title,
  subtitle,
  href,
  linkLabel = "Xem tất cả",
}: {
  title: string;
  subtitle?: string;
  href?: string;
  linkLabel?: string;
}) {
  return (
    <div className="mb-6 flex items-end justify-between gap-4">
      <div>
        <h2 className="text-xl font-extrabold tracking-tight sm:text-2xl">
          {title}
        </h2>
        {subtitle && (
          <p className="mt-1 text-sm text-muted-foreground">{subtitle}</p>
        )}
      </div>
      {href && (
        <Link
          href={href}
          className="flex shrink-0 items-center gap-1 text-sm font-semibold text-primary hover:underline"
        >
          {linkLabel} <ArrowRight size={16} />
        </Link>
      )}
    </div>
  );
}
