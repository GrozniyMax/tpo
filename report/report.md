## Вариант
> Вариант: 4356726

![img.png](img.png)

Код доступен по [ссылке](https://github.com/GrozniyMax/tpo.git) в ветке lab2

### Цель
Реализовать и провести интеграционное тестирование функции вычисления выражения с заранее заданой стратегией интеграции.

## Архитектура

## Диаграммы зависимости модулей

### Диаграмма зависимостей модулей (Negative Expression)

```mermaid
graph TD
    subgraph "Negative Modules (x <= 0)"
        NE[negativeExpression]
        M6[module6Numerator]
        M5[module5]
        M4N[module4Numerator]
        M4D[module4Denominator]
        M3[module3]
        M2[module2]
        M1[module1]
    end

    subgraph "Basic Trigonometry"
        cot[cot]
        csc[csc]
        sec[sec]
        sin[sin]
        cos[cos]
    end

    NE --> M6
    NE --> cot
    M6 --> M5
    M6 --> sin
    M5 --> M4N
    M5 --> M4D
    M4N --> M1
    M4N --> M2
    M4N --> M3
    M4D --> cot
    M4D --> csc
    M4D --> sec
    M1 --> csc
    M1 --> sec
    M1 --> sin
    M1 --> cot
    M2 --> cos
    M2 --> csc
    M3 --> sec
    M3 --> sin
```

### Диаграмма зависимостей модулей (Positive Expression)

```mermaid
graph TD
    subgraph "Positive Modules (x > 0)"
        PE[positiveExpression]
        AL5[addLog5]
        LNC[lnCubed]
        AL3[addLog3]
        MB2[multiplyByLog2]
        L10S[log10Squared]
    end

    subgraph "Basic Logarithms"
        ln[ln]
        log[log]
    end

    PE --> AL5
    PE --> LNC
    AL5 --> AL3
    AL5 --> log
    LNC --> ln
    AL3 --> MB2
    AL3 --> log
    MB2 --> L10S
    MB2 --> log
    L10S --> log
```

### Диаграмма зависимостей базовых функций

```mermaid
graph TD
    subgraph "BasicTrigonometry.kt"
        cos[cos]
        sin[sin]
        sec[sec]
        csc[csc]
        cot[cot]
    end

    subgraph "BasicLogarithms.kt"
        ln[ln]
        log[log]
    end

%% Trigonometry dependencies
    sin --> cos
    sec --> cos
    csc --> sin
    cot --> cos
    cot --> sin
%% Logarithms dependencies
    log --> ln
```

### Детальное описание зависимостей базовых функций

#### BasicTrigonometry.kt

| Функция  | Зависимости  | Описание                                |
|----------|--------------|-----------------------------------------|
| `cos(x)` | -            | Базовая функция, использует ряд Тейлора |
| `sin(x)` | `cos`        | Вычисляется через cos(π/2 - x)          |
| `sec(x)` | `cos`        | sec(x) = 1 / cos(x)                     |
| `csc(x)` | `sin`        | csc(x) = 1 / sin(x)                     |
| `cot(x)` | `cos`, `sin` | cot(x) = cos(x) / sin(x)                |

#### BasicLogarithms.kt

| Функция        | Зависимости | Описание                                        |
|----------------|-------------|-------------------------------------------------|
| `ln(x)`        | -           | Базовая функция, использует ряд t = (x-1)/(x+1) |
| `log(x, base)` | `ln`        | log_base(x) = ln(x) / ln(base)                  |

## Разбиение по модулям

### Модули для вычисления при отрицательном аргументе

#### Модуль 1

> Формула: ((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) ^ 3

Вольфрам альфа

```
https://www.wolframalpha.com/input?i=f(x)+%3D+((((csc(x)+/+csc(x))+/+sec(x))+-+sin(x))+++cot(x))+%5E+3
```

#### Модуль 2

> Формула: cos(x) + csc(x)

[WolframAlpha](https://www.wolframalpha.com/input?i=f(x)+%3D+cos(x)+++csc(x))

#### Модуль 3

> Формула:

[WolframAlpha](https://www.wolframalpha.com/input?i=f(x)+%3D+sec(x)+-+sin(x)+at+x+%3D+5.1)

#### Модуль 4(Числитель)

> Формула: (((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) + cos(x) + csc(x)) - (sec(x) - sin(x))

Вольфрам альфа

```
https://www.wolframalpha.com/input?i=f(x)+%3D+(((((csc(x)+/+csc(x))+/+sec(x))+-+sin(x))+++cot(x))+++cos(x)+++csc(x))+-+(sec(x)+-+sin(x))
```

#### Модуль 4(Знаменатель)

> Формула: (cot(x) ^ 2) / (csc(x) + sec(x))

Вольфрам альфа

```
https://www.wolframalpha.com/input?i=f(x)+%3D+(cot(x)+%5E+2)+/+(csc(x)+++sec(x))+at+x+%3D+0.1
```

#### Модуль 5

> Формула: ((((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) + cos(x) + csc(x)) - (sec(x) - sin(x))) / ((cot(x) ^

2) / (csc(x) + sec(x)))

Вольфрам альфа

```
https://www.wolframalpha.com/input?i=((((((csc(x)+/+csc(x))+/+sec(x))+-+sin(x))+++cot(x))+++cos(x)+++csc(x))+-+(sec(x)+-+sin(x)))+/+((cot(x)+%5E+2)+/+(csc(x)+++sec(x)))
```

### Модуль 6 (Числитель)

> Формула: ((((((((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) ^ 3) + (cos(x) + csc(x))) - (sec(x) - sin(x))) / ((
> cot(x) ^ 2) / (csc(x) + sec(x)))) * (sin(x) ^ 2)) ^ 2)

Вольфрам альфа

```
https://www.wolframalpha.com/input?i=f(x)%3D((((((((((csc(x)+/+csc(x))+/+sec(x))+-+sin(x))+++cot(x))+%5E+3)+++(cos(x)+++csc(x)))+-+(sec(x)+-+sin(x)))+/+((cot(x)+%5E+2)+/+(csc(x)+++sec(x))))+*+(sin(x)+%5E+2))+%5E+2)
```

### Модули для вычисления при положительном аргументе

#### Модуль 1

> Формула: log₁₀(x) × log₁₀(x) = (log₁₀(x))²

[WolframAlpha](https://www.wolframalpha.com/input?i2d=true&i=Power%5BLog%5B10,x%5D,2%5D)

#### Модуль 2

> Формула: log₁₀(x))² × log₂(x)

[WolframAlpha](https://www.wolframalpha.com/input?i2d=true&i=Power%5BLog%5B10,x%5D,2%5D+*+Log%5B2,x%5D)

#### Модуль 3

> Формула: (log₁₀(x))² × log₂(x) + log₃(x)

[WolframAlpha](https://www.wolframalpha.com/input?i2d=true&i=Power%5BLog%5B10,x%5D,2%5D+*+Log%5B2,x%5D+++Log%5B3,x%5D)

#### Модуль 4

> Формула: (log₁₀(x))² × log₂(x) + log₃(x) + log₅(x)

[WolframAlpha](https://www.wolframalpha.com/input?i2d=true&i=Power%5BLog%5B10,x%5D,2%5D+*+Log%5B2,x%5D+++Log%5B3,x%5D+++Log%5B5,x%5D)

#### Модуль 5

> Формула: ln(x)³ = (ln(x))³

[WolframAlpha](https://www.wolframalpha.com/input?i=ln(x)%5E3)

## Графики функций

### График основной функции

![График основной функции](graphics/main.png)

### Графики тригонометрических функций

#### Синус (sin)

![График функции sin(x)](graphics/sin.png)

#### Косинус (cos)

![График функции cos(x)](graphics/cos.png)

#### Котангенс (cot)

![График функции cot(x)](graphics/cot.png)

#### Секанс (sec)

![График функции sec(x)](graphics/sec.png)

#### Косеканс (csc)

![График функции csc(x)](graphics/csc.png)

## Тестирование

### Юнит тесты

1. Базовое тестирование функцию на ожидаемый результат (package `basic`)
2. Отладочные тесты без мокирования для ветки с отрицательным аргументом (package `notMocked`)

### Интеграционное тестирование
> Для замены компонентов использовался MockK

1. Тестирование модулей в изоляции. Для каждого модуля мокируются все функции от которых он зависит. Это позволяет
   проверить каждый модуль в изоляции.
2. Тестирование вычислений с мокированием базовых функций для негативной ветки
    1. Мокирование csc и cot
    2. Мокирование sin и sec
    3. Мокирование cos
    4. Без моков
3. Тестирование вычислений с мокированием базовых функций для положительной функции ветки
    1. Мокирование logₐ
    2. Мокирование ln
    3. Без моков

## Выводы

В рамках выполнения лабораторной работы была реализована система математических функций для вычисления выражения по варианту. Также было проведено модульное и интеграционное тестирование с использованием JUnit5 и MockK. Это позволило мне закрепить свои навыки использования этих инструментов и углубить их понимание.