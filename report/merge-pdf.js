import { PDFDocument } from 'pdf-lib';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const reportDir = __dirname;

async function mergePDFs() {
    const titlePageFile = path.join(reportDir, 'ТПОЛаб3.pdf');
    const reportFile = path.join(reportDir, 'Лабораторная_работа_3_Отчет.pdf');
    const outputFile = path.join(reportDir, 'Лабораторная_работа_3_Итоговый.pdf');
    
    console.log('Merging PDFs...');
    
    // Check if files exist
    if (!fs.existsSync(titlePageFile)) {
        console.error(`Title page file not found: ${titlePageFile}`);
        return;
    }
    
    if (!fs.existsSync(reportFile)) {
        console.error(`Report file not found: ${reportFile}`);
        return;
    }
    
    try {
        // Read the title page PDF
        const titlePagePdfBytes = fs.readFileSync(titlePageFile);
        const titlePagePdf = await PDFDocument.load(titlePagePdfBytes);
        
        // Read the report PDF
        const reportPdfBytes = fs.readFileSync(reportFile);
        const reportPdf = await PDFDocument.load(reportPdfBytes);
        
        // Create a new PDF document
        const mergedPdf = await PDFDocument.create();
        
        // Copy title page (first page only)
        const [titlePage] = await mergedPdf.copyPages(titlePagePdf, [0]);
        mergedPdf.addPage(titlePage);
        
        // Copy all pages from the report
        const reportPages = await mergedPdf.copyPages(
            reportPdf, 
            reportPdf.getPageIndices()
        );
        reportPages.forEach(page => mergedPdf.addPage(page));
        
        // Save the merged PDF
        const mergedPdfBytes = await mergedPdf.save();
        fs.writeFileSync(outputFile, mergedPdfBytes);
        
        console.log(`PDF merged successfully: ${outputFile}`);
        console.log(`Total pages: ${mergedPdf.getPageCount()}`);
        
    } catch (error) {
        console.error('Error merging PDFs:', error);
        throw error;
    }
}

mergePDFs().catch(console.error);
