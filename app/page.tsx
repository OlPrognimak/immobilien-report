import { ArrowRight, Building2, FileText } from "lucide-react";
import Link from "next/link";

const options = [
  {
    href: "/xsl-fo",
    title: "XSL-FO",
    description: "Use the existing Apache FOP implementation to generate property portfolio PDFs.",
    badge: "Current solution",
  },
  {
    href: "/jasper-report",
    title: "JasperReport",
    description: "Use the new JasperReports implementation to generate the same portfolio report.",
    badge: "New implementation",
  },
];

export default function Home() {
  return (
    <main className="min-h-screen bg-[#f2f5f7] text-[#17212b]">
      <header className="border-b border-[#d7dee3] bg-[#102a43] text-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-5 sm:px-8">
          <div className="flex items-center gap-3">
            <span className="grid h-10 w-10 place-items-center rounded-lg bg-[#d4a64a] text-[#102a43]">
              <Building2 aria-hidden="true" size={22} strokeWidth={2.25} />
            </span>
            <div>
              <p className="text-lg font-semibold tracking-tight">ImmoReport</p>
              <p className="text-sm text-[#c6d3df]">Property portfolio documents</p>
            </div>
          </div>
          <span className="hidden rounded-full border border-white/20 px-3 py-1 text-xs font-medium text-[#dce6ee] sm:inline">
            PDF renderer selection
          </span>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-5 py-10 sm:px-8 sm:py-14">
        <div className="mb-7 max-w-2xl">
          <p className="mb-2 text-sm font-semibold uppercase tracking-[0.14em] text-[#8c6825]">Report generation</p>
          <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">Choose a PDF implementation</h1>
          <p className="mt-4 text-sm leading-6 text-[#5b6772]">
            Select the renderer first, then choose the company report to open.
          </p>
        </div>

        <div className="grid gap-5 md:grid-cols-2">
          {options.map((option) => (
            <Link
              key={option.href}
              href={option.href}
              className="group rounded-lg border border-[#d7dee3] bg-white p-6 shadow-[0_8px_30px_rgba(16,42,67,0.06)] transition hover:border-[#b9c5ce] hover:bg-[#fbfcfd] focus:outline-none focus:ring-2 focus:ring-[#d4a64a] focus:ring-offset-2"
            >
              <div className="mb-5 flex items-center justify-between gap-4">
                <span className="grid h-11 w-11 place-items-center rounded-md bg-[#edf2f5] text-[#315470]">
                  <FileText size={21} aria-hidden="true" />
                </span>
                <span className="rounded-full bg-[#eef3f6] px-3 py-1 text-xs font-semibold text-[#50606e]">{option.badge}</span>
              </div>
              <h2 className="text-2xl font-semibold tracking-tight">{option.title}</h2>
              <p className="mt-3 min-h-12 text-sm leading-6 text-[#5b6772]">{option.description}</p>
              <span className="mt-6 inline-flex items-center gap-2 text-sm font-semibold text-[#102a43]">
                Select {option.title}
                <ArrowRight size={16} aria-hidden="true" className="transition group-hover:translate-x-1" />
              </span>
            </Link>
          ))}
        </div>
      </section>
    </main>
  );
}
