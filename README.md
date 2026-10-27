# Cryptography laboratories

Java 17+ Maven project. Open the root `pom.xml` in IntelliJ IDEA and select a **JDK 17 or newer** for both the project SDK and Maven runner. IntelliJ can use its bundled Maven; a separate Maven installation is optional. No IDE project files are committed.

## Build and run

From the repository root:

```sh
mvn package
java -jar lab1/target/lab1-1.0-SNAPSHOT.jar
```

Run tests only with `mvn test`. In IntelliJ, run `md.utm.cryptography.lab1.Main` directly, or run the parent project's Maven `package` lifecycle.

`lab1` implements both Romanian Caesar cipher tasks: the standard 31-letter alphabet and a keyword permutation. The numeric shift must be 1–30. Keywords require at least seven letters, with no spaces. Messages accept Romanian letters and ordinary spaces; output is uppercase without spaces. Romanian cedilla variants and decomposed diacritics are normalized. Invalid inputs are explained and requested again. Choose `0` in the cipher menu to exit.

## Laboratory reports

Both reports retain the same UTM template and share their formatting and metadata:

| Laboratory | PDF | Source | Supporting materials |
| --- | --- | --- | --- |
| 1 — Caesar cipher | [lab1.pdf](reports/lab1.pdf) | [lab1.tex](lab1/lab1.tex) | [Screenshots and transcripts](lab1/screenshots/), [Java source](lab1/src/) |
| 2 — Cryptanalysis of monoalphabetic ciphers | [lab2.pdf](reports/lab2.pdf) | [lab2.tex](lab2/lab2.tex) | [Frequency screenshots](lab2/screenshots/), [ciphertext and plaintext](lab2/materials/) |

Lab 2 is report-only, using Variant 1 from the supplied reference. It includes four reused frequency screenshots, the 19-step substitution analysis, and complete ciphertext and plaintext; it has no Maven module.

### Shared report structure

- `reports/` stores compiled PDFs named `labN.pdf`.
- `metadata.tex` holds shared student, group, course, instructor, institution, city, year, and repository details.
- `build-reports.sh` lists the lab sources and runs Tectonic from each lab directory, writing PDFs to `reports/`.
- `latex/utmreport.cls` defines the existing layout, typography, title page, headings, captions, and code listings.
- `latex/report-common.tex` loads metadata and applies shared title-page settings, PDF metadata, and the handout reference helper.
- `latex/assets/` contains the shared university logo.
- Each `labN/` holds `labN.tex`, screenshots, evidence, and any supporting code or notes.

### Build reports

Install Tectonic and the template's fonts (Times New Roman or Liberation Serif, plus Liberation Mono), then run:

```sh
./build-reports.sh
```

The script also works when invoked from another directory. Extra arguments are forwarded to Tectonic. On this workstation, the offline build uses cached dependencies and installed math fonts:

```sh
./build-reports.sh --only-cached -Z search-path=/usr/share/fonts/texlive-amsfonts
```

### Add a report

Create `labN/labN.tex` with this preamble and your report content:

```tex
\documentclass{utmreport}
\newcommand{\ReportTitle}{Your report title}
% Optional: \newcommand{\ReportSubtitle}{\ReportCourse: your topic}
\input{../latex/report-common.tex}
\begin{document}
\makeutmreporttitlepage
\tableofcontents
\clearpage
\utmintroduction
Your report content.
\utmconclusions
Your conclusions.
\end{document}
```

The report number comes from the `labN.tex` filename; it can be overridden by defining `\ReportNumber` before loading the common file. Keep lab-specific listing settings and other overrides after the common input. Paths to screenshots and materials are relative to the lab directory.

Add `labN/labN.tex` to the loop in `build-reports.sh`, run the script, and add PDF, source, and supporting-material links to the table above. To adapt this structure to another course, update `metadata.tex` and any course-specific wording in `\ReportHandoutReference` in `latex/report-common.tex`.

## Add another programming laboratory

For a future lab requiring code, create its own POM inheriting the root parent, add its source directories, and register it in the root POM. Report-only labs do not need Maven changes.

Build output, LaTeX temporary files, local IDE settings and the original template directory are excluded through `.gitignore`. Console evidence and template dependencies are kept in Git.
