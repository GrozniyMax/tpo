import plantumlEncoder from 'plantuml-encoder';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import fetch from 'node-fetch';
import sharp from 'sharp';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const reportDir = __dirname;

// PlantUML diagrams from use-case.md
const diagrams = [
    {
        name: 'use-case-full',
        code: `@startuml Booking.com Use-Case Diagram
left to right direction
skinparam packageStyle rectangle

actor "Пользователь" as User
actor "Система" as System

rectangle "Booking.com" {

  package "Поиск Размещения" {
    usecase "UC-01\\nПоиск жилья по городу" as UC01
    usecase "UC-02\\nФильтрация и сортировка\\nрезультатов" as UC02
    usecase "UC-03\\nПросмотр деталей\\nпредложения" as UC03
    usecase "UC-04\\nНавигация назад\\nк результатам" as UC04
  }

  package "Поиск Авиабилетов" {
    usecase "UC-05\\nПоиск рейсов" as UC05
  }

  package "Поиск Досуга" {
    usecase "UC-06\\nПоиск экскурсий\\nпо направлению" as UC06
    usecase "UC-07\\nФильтрация и сортировка\\nэкскурсий" as UC07
  }

  package "Общие Операции" {
    usecase "Отклонение\\nавторизации" as Auth
    usecase "Отклонение\\ncookie-баннера" as Cookie
    usecase "Ввод\\nпараметров поиска" as Input
    usecase "Выбор\\nфильтра" as Filter
    usecase "Выбор\\nсортировки" as Sort
  }
}

User --> UC01
User --> UC02
User --> UC03
User --> UC04
User --> UC05
User --> UC06
User --> UC07

System --> UC01
System --> UC02
System --> UC03
System --> UC04
System --> UC05
System --> UC06
System --> UC07

UC01 ..> Auth : <<include>>
UC01 ..> Input : <<include>>
UC01 ..> Cookie : <<include>>

UC02 ..> Auth : <<include>>
UC02 ..> Filter : <<include>>
UC02 ..> Sort : <<include>>

UC03 ..> UC04 : <<extend>>
UC04 ..> Auth : <<include>>

UC05 ..> Auth : <<include>>
UC05 ..> Input : <<include>>
UC05 ..> Cookie : <<include>>

UC06 ..> Auth : <<include>>
UC06 ..> Input : <<include>>
UC06 ..> Cookie : <<include>>

UC07 ..> Cookie : <<include>>
UC07 ..> Filter : <<include>>
UC07 ..> Sort : <<include>>

@enduml`
    },
    {
        name: 'use-case-simple',
        code: `@startuml Booking.com Use-Case Diagram Simple
left to right direction
skinparam packageStyle rectangle
skinparam actorStyle awesome

actor "Пользователь" as User
actor "Система" as System

rectangle "Booking.com" {

  package "Поиск Размещения" {
    usecase "UC-01\\nПоиск жилья по городу" as UC01
    usecase "UC-02\\nФильтрация и сортировка" as UC02
    usecase "UC-03\\nПросмотр деталей" as UC03
    usecase "UC-04\\nНавигация назад" as UC04
  }

  package "Поиск Авиабилетов" {
    usecase "UC-05\\nПоиск рейсов" as UC05
  }

  package "Поиск Досуга" {
    usecase "UC-06\\nПоиск досуга" as UC06
    usecase "UC-07\\nФильтрация досуга" as UC07
  }
}

User --> UC01
User --> UC02
User --> UC03
User --> UC04
User --> UC05
User --> UC06
User --> UC07

System --> UC01
System --> UC02
System --> UC03
System --> UC04
System --> UC05
System --> UC06
System --> UC07

UC01 ..> UC02 : <<extend>>
UC03 ..> UC04 : <<extend>>
UC06 ..> UC07 : <<extend>>

@enduml`
    }
];

async function renderDiagram(diagram, outputDir) {
    try {
        console.log(`Rendering diagram: ${diagram.name}...`);
        
        // Encode the PlantUML code
        const encoded = plantumlEncoder.encode(diagram.code);
        
        // Use Kroki API to render
        const url = `https://kroki.io/plantuml/svg/${encoded}`;
        
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const svgBuffer = await response.buffer();
        
        // Save SVG
        const svgPath = path.join(outputDir, `${diagram.name}.svg`);
        fs.writeFileSync(svgPath, svgBuffer);
        console.log(`Saved SVG: ${svgPath}`);
        
        // Convert SVG to PNG using sharp
        const pngPath = path.join(outputDir, `${diagram.name}.png`);
        
        await sharp(svgBuffer)
            .resize(1400, null, { fit: 'inside' })
            .png()
            .toFile(pngPath);
        
        console.log(`Saved PNG: ${pngPath}`);
        
        return true;
        
    } catch (error) {
        console.error(`Error rendering ${diagram.name}:`, error.message);
        return false;
    }
}

async function renderDiagrams() {
    const outputDir = path.join(reportDir, 'img');
    
    // Create output directory if it doesn't exist
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
        console.log(`Created directory: ${outputDir}`);
    }

    let successCount = 0;
    
    for (const diagram of diagrams) {
        const success = await renderDiagram(diagram, outputDir);
        if (success) successCount++;
    }
    
    console.log(`\nDiagram rendering complete! ${successCount}/${diagrams.length} diagrams rendered.`);
}

renderDiagrams();
