const fs = require('fs');
const path = require('path');
const PDFMerger = require('pdf-merger-js');

const reportDir = path.join(__dirname);
const merger = new PDFMerger.default();

async function mergePDFs() {
    const lab1Path = path.join(reportDir, 'ТПОЛаб1.pdf');
    const lab2Path = path.join(reportDir, 'report-lab2.pdf');
    const outputPath = path.join(reportDir, 'ТПО_ЛР2.pdf');

    // Check if both PDFs exist
    if (!fs.existsSync(lab1Path)) {
        console.error('Error: ТПОЛаб1.pdf not found');
        process.exit(1);
    }
    
    if (!fs.existsSync(lab2Path)) {
        console.error('Error: report-lab2.pdf not found');
        process.exit(1);
    }

    console.log('Merging PDFs...');
    console.log('1. Adding ТПОЛаб1.pdf...');
    await merger.add(lab1Path);
    
    console.log('2. Adding report-lab2.pdf...');
    await merger.add(lab2Path);
    
    console.log('3. Writing merged PDF...');
    await merger.save(outputPath);
    
    console.log('Done! Created ТПО_ЛР2.pdf');
    console.log(`   - ТПОЛаб1.pdf (first)`);
    console.log(`   - report-lab2.pdf (second)`);
}

mergePDFs().catch(err => {
    console.error('Error merging PDFs:', err);
    process.exit(1);
});
