"use client";

import { Building2, ExternalLink, FileText, LoaderCircle, TriangleAlert } from "lucide-react";
import { useEffect, useState } from "react";

type Company = { id: number; name: string; city: string; propertyCount: number };
const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export default function Home() {
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const controller = new AbortController();
    fetch(`${API_URL}/api/companies`, { signal: controller.signal })
      .then(async (response) => {
        if (!response.ok) throw new Error(`Request failed (${response.status})`);
        return response.json() as Promise<Company[]>;
      })
      .then(setCompanies)
      .catch((reason: unknown) => {
        if (reason instanceof DOMException && reason.name === "AbortError") return;
        setError(reason instanceof Error ? reason.message : "The report service is unavailable.");
      })
      .finally(() => setLoading(false));
    return () => controller.abort();
  }, []);

  return (
    <main className="min-h-screen bg-[#f2f5f7] text-[#17212b]">
      <header className="border-b border-[#d7dee3] bg-[#102a43] text-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-5 sm:px-8">
          <div className="flex items-center gap-3">
            <span className="grid h-10 w-10 place-items-center rounded-lg bg-[#d4a64a] text-[#102a43]">
              <Building2 aria-hidden="true" size={22} strokeWidth={2.25} />
            </span>
            <div><p className="text-lg font-semibold tracking-tight">ImmoReport</p><p className="text-sm text-[#c6d3df]">Property portfolio documents</p></div>
          </div>
          <span className="hidden rounded-full border border-white/20 px-3 py-1 text-xs font-medium text-[#dce6ee] sm:inline">MVP · H2 / XSL-FO</span>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-5 py-10 sm:px-8 sm:py-14">
        <div className="mb-7 flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
          <div><p className="mb-2 text-sm font-semibold uppercase tracking-[0.14em] text-[#8c6825]">Portfolio reports</p><h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">Companies</h1></div>
          <p className="max-w-md text-sm leading-6 text-[#5b6772]">Open a company report to generate a current PDF containing every property stored in the in-memory database.</p>
        </div>

        <div className="overflow-hidden rounded-xl border border-[#d7dee3] bg-white shadow-[0_8px_30px_rgba(16,42,67,0.06)]">
          {loading && <div className="flex min-h-48 items-center justify-center gap-3 text-[#5b6772]" role="status"><LoaderCircle className="animate-spin" size={20} /> Loading companies…</div>}
          {error && <div className="flex min-h-48 flex-col items-center justify-center gap-3 px-6 text-center" role="alert"><TriangleAlert className="text-[#b45309]" size={24} /><div><p className="font-semibold">Could not load the companies</p><p className="mt-1 text-sm text-[#66727d]">{error} Start the Spring Boot API on port 8080.</p></div></div>}
          {!loading && !error && companies.length === 0 && <div className="grid min-h-48 place-items-center px-6 text-center text-[#66727d]">No companies are available.</div>}
          {!loading && !error && companies.length > 0 && (
            <div className="overflow-x-auto">
              <table className="w-full min-w-[640px] border-collapse text-left">
                <thead className="bg-[#eaf0f4] text-xs font-semibold uppercase tracking-[0.09em] text-[#50606e]"><tr><th className="px-6 py-4">Company</th><th className="px-6 py-4">Location</th><th className="px-6 py-4">Properties</th><th className="px-6 py-4 text-right">Report</th></tr></thead>
                <tbody className="divide-y divide-[#e2e8ec]">
                  {companies.map((company) => (
                    <tr key={company.id} className="transition-colors hover:bg-[#f8fafb]">
                      <td className="px-6 py-5"><div className="flex items-center gap-3"><span className="grid h-9 w-9 place-items-center rounded-md bg-[#edf2f5] text-[#315470]"><Building2 size={18} aria-hidden="true" /></span><span className="font-semibold">{company.name}</span></div></td>
                      <td className="px-6 py-5 text-[#5b6772]">{company.city}</td>
                      <td className="px-6 py-5"><span className="rounded-full bg-[#eef3f6] px-3 py-1 text-sm font-medium">{company.propertyCount}</span></td>
                      <td className="px-6 py-5 text-right"><a className="inline-flex items-center gap-2 rounded-md bg-[#102a43] px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-[#1c4160] focus:outline-none focus:ring-2 focus:ring-[#d4a64a] focus:ring-offset-2" href={`${API_URL}/api/companies/${company.id}/report`} target="_blank" rel="noreferrer"><FileText size={16} aria-hidden="true" /> Open PDF <ExternalLink size={14} aria-hidden="true" /></a></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}
