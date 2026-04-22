.PHONY: clean diagrams pdf merge report all

# Kill browser processes
clean:
	pkill -f "Google Chrome" || true
	pkill -f "Firefox" || true

# Render PlantUML diagrams from use-case.md
diagrams:
	node report/render-diagrams.js

# Generate PDF from markdown report
pdf:
	node report/generate-pdf.js

# Merge title page with report PDF
merge:
	node report/merge-pdf.js

# Generate complete report (diagrams + pdf + merge)
report: diagrams pdf merge

# Alias for report
all: report