import pdf from 'html-pdf';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { marked } from 'marked';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const reportDir = __dirname;

// Convert image to base64 data URI
function imageToBase64(imagePath) {
    try {
        const absolutePath = path.isAbsolute(imagePath) ? imagePath : path.join(reportDir, imagePath);
        const imageBuffer = fs.readFileSync(absolutePath);
        const ext = path.extname(absolutePath).toLowerCase();
        const mimeTypes = {
            '.png': 'image/png',
            '.jpg': 'image/jpeg',
            '.jpeg': 'image/jpeg',
            '.gif': 'image/gif',
            '.svg': 'image/svg+xml',
            '.webp': 'image/webp'
        };
        const mimeType = mimeTypes[ext] || 'image/png';
        const base64 = imageBuffer.toString('base64');
        return `data:${mimeType};base64,${base64}`;
    } catch (error) {
        console.error(`Error loading image ${imagePath}:`, error.message);
        return '';
    }
}

// Process HTML to replace image paths with base64
function processImages(htmlContent) {
    return htmlContent.replace(/<img\s+[^>]*src=["']([^"']+)["'][^>]*>/gi, (match, src) => {
        // Skip data URIs and external URLs
        if (src.startsWith('data:') || src.startsWith('http://') || src.startsWith('https://')) {
            return match;
        }
        
        // Convert relative path to base64
        const dataUri = imageToBase64(src);
        if (dataUri) {
            return match.replace(src, dataUri);
        }
        return match;
    });
}

async function generatePDF() {
    const inputFile = path.join(reportDir, 'Лабораторная_работа_3_Полный_отчет.md');
    const outputFile = path.join(reportDir, 'Лабораторная_работа_3_Отчет.pdf');

    console.log('Generating PDF from markdown...');

    // Read the markdown file
    const markdownContent = fs.readFileSync(inputFile, 'utf-8');

    // Convert markdown to HTML
    let htmlContent = marked.parse(markdownContent);

    // Process images - convert to base64
    htmlContent = processImages(htmlContent);

    // Create full HTML with styles
    const fullHTML = `
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отчет по лабораторной работе №3</title>
</head>
<body style="font-family: 'Times New Roman', Times, serif; font-size: 12pt; line-height: 1.3; color: #000; max-width: 210mm; margin: 0 auto; padding: 10px;">
    <style>
        @page {
            margin: 1.5cm;
            size: A4;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 10px 0;
            font-size: 10pt;
        }
        th, td {
            border: 1px solid #000;
            padding: 5px;
            text-align: left;
        }
        th {
            background-color: #f0f0f0;
            font-weight: bold;
        }
        pre {
            background-color: #f5f5f5;
            padding: 8px;
            border-radius: 3px;
            overflow-x: auto;
            font-family: 'Courier New', Courier, monospace;
            font-size: 9pt;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        code {
            font-family: 'Courier New', Courier, monospace;
            background-color: #f5f5f5;
            padding: 2px 3px;
            border-radius: 2px;
            font-size: 10pt;
        }
        pre code {
            background-color: transparent;
            padding: 0;
        }
        img {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 10px auto;
            page-break-inside: avoid;
        }
        h1 { font-size: 14pt; text-align: center; margin-bottom: 8px; }
        h2 { font-size: 12pt; margin-top: 16px; margin-bottom: 8px; }
        h3 { font-size: 11pt; margin-top: 12px; margin-bottom: 6px; }
        p { margin: 5px 0; text-align: justify; }
        ul, ol { margin: 5px 0; padding-left: 20px; }
        li { margin: 3px 0; }
        blockquote {
            margin: 8px 0;
            padding: 5px 12px;
            border-left: 3px solid #ccc;
            background-color: #f9f9f9;
            font-size: 11pt;
        }
        hr { 
            border: none; 
            border-top: 1px solid #ccc; 
            margin: 16px 0; 
            page-break-before: always; 
        }
        /* Hide first hr if it's at the beginning of the document */
        hr:first-child {
            display: none;
        }
        a { color: #0066cc; text-decoration: none; }
    </style>
    ${htmlContent}
</body>
</html>
    `;

    const options = {
        format: 'A4',
        orientation: 'portrait',
        border: {
            top: '1.5cm',
            right: '1.5cm',
            bottom: '1.5cm',
            left: '1.5cm'
        },
        timeout: 60000
    };

    return new Promise((resolve, reject) => {
        pdf.create(fullHTML, options).toFile(outputFile, (err, result) => {
            if (err) {
                console.error('Error generating PDF:', err);
                reject(err);
            } else {
                console.log(`PDF generated successfully: ${outputFile}`);
                console.log(`File size: ${result.filename}`);
                resolve(result);
            }
        });
    });
}

generatePDF().catch(console.error);
