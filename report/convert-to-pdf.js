const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const reportDir = path.join(__dirname);
const mdPath = path.join(reportDir, 'report.md');
const imgDir = path.join(reportDir, 'generated-diagrams');

// Create directory for generated diagrams
if (!fs.existsSync(imgDir)) {
    fs.mkdirSync(imgDir, { recursive: true });
}

// Read the markdown file
let mdContent = fs.readFileSync(mdPath, 'utf-8');

// Find all mermaid code blocks
const mermaidRegex = /```mermaid\n([\s\S]*?)\n```/g;
let match;
let diagramIndex = 0;
const diagramFiles = [];

// Extract and convert each mermaid diagram
while ((match = mermaidRegex.exec(mdContent)) !== null) {
    const mermaidCode = match[1];
    const diagramFile = path.join(imgDir, `diagram-${diagramIndex}.png`);
    const tempMermaidFile = path.join(imgDir, `diagram-${diagramIndex}.mmd`);
    
    // Write mermaid code to temp file
    fs.writeFileSync(tempMermaidFile, mermaidCode);
    
    // Convert to PNG using mmdc
    try {
        execSync(`npx mmdc -i "${tempMermaidFile}" -o "${diagramFile}" -b transparent`, {
            stdio: 'inherit',
            cwd: reportDir
        });
        
        // Replace mermaid block with image reference
        const relativeImgPath = `./generated-diagrams/diagram-${diagramIndex}.png`;
        const imageMarkdown = `![Diagram ${diagramIndex + 1}](${relativeImgPath})`;
        
        // Store for replacement
        diagramFiles.push({
            original: match[0],
            replacement: imageMarkdown
        });
        
        diagramIndex++;
    } catch (error) {
        console.error(`Error converting diagram ${diagramIndex}:`, error.message);
    }
}

// Replace all mermaid blocks with image references
diagramFiles.forEach(({ original, replacement }) => {
    mdContent = mdContent.replace(original, replacement);
});

// Write modified markdown
const tempMdPath = path.join(reportDir, 'report-with-images.md');
fs.writeFileSync(tempMdPath, mdContent);

console.log('Converting markdown to PDF...');

// Convert to PDF using mdpdf with proper path handling
try {
    const destPath = path.join(reportDir, 'report-lab2.pdf');
    execSync(`npx mdpdf "${tempMdPath}" "${destPath}" --no-emoji`, {
        stdio: 'inherit',
        cwd: process.cwd()
    });
    console.log('PDF created successfully!');
} catch (error) {
    console.error('Error creating PDF:', error.message);
    process.exit(1);
}

// Clean up temp files
try {
    fs.unlinkSync(tempMdPath);
} catch (e) {}

fs.readdirSync(imgDir).forEach(file => {
    if (file.endsWith('.mmd')) {
        fs.unlinkSync(path.join(imgDir, file));
    }
});

console.log('Done! Created report-lab2.pdf with mermaid diagrams as images.');
