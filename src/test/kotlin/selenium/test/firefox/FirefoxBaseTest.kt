package selenium.test.firefox

import org.apache.commons.lang3.ObjectUtils.mode
import selenium.Mode
import selenium.SeleniumBaseTest

abstract class FirefoxBaseTest(useHeadless: Boolean = false) :
    SeleniumBaseTest(mode = Mode.FIREFOX, useHeadless = useHeadless) {
}