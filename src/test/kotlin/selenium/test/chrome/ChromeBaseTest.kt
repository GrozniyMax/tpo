package selenium.test.chrome

import selenium.Mode
import selenium.SeleniumBaseTest

abstract class ChromeBaseTest(useHeadless: Boolean = true) :
    SeleniumBaseTest(mode = Mode.CHROME, useHeadless = useHeadless) {
}