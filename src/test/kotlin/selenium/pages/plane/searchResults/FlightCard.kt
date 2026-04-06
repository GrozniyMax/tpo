package selenium.pages.plane.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement

data class FlightCard(val airCompany: String, val flights: List<Flight>)
data class Flight(val departure: Airport, val arrival: Airport, val duration: String)
data class Airport(val code: String, val time: String, val day: String)

object PlaneCardParser {

    fun parse(card: WebElement): FlightCard {
        val airCompany = card.findElementOrNull(
            By.xpath(".//*[@data-testid='flight_card_carriers']")
        )?.text ?: ""

        return FlightCard(airCompany = airCompany, flights = parseFlights(card))
    }

    private fun parseFlights(card: WebElement): List<Flight> {
        val departureTimeElements = card.findElements(
            By.xpath(".//*[starts-with(@data-testid, 'flight_card_segment_departure_time_')]")
        )
        return departureTimeElements.mapIndexed { index, _ -> parseSegment(card, index) }
    }

    private fun parseSegment(card: WebElement, segmentIndex: Int): Flight {
        return Flight(
            departure = Airport(
                code = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_departure_airport_$segmentIndex']"))?.text ?: "",
                time = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_departure_time_$segmentIndex']//div"))?.text ?: "",
                day = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_departure_date_$segmentIndex']"))?.text ?: ""
            ),
            arrival = Airport(
                code = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_destination_airport_$segmentIndex']"))?.text ?: "",
                time = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_destination_time_$segmentIndex']//div"))?.text ?: "",
                day = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_destination_date_$segmentIndex']"))?.text ?: ""
            ),
            duration = card.findElementOrNull(By.xpath(".//*[@data-testid='flight_card_segment_duration_$segmentIndex']"))?.text ?: ""
        )
    }

    private fun WebElement.findElementOrNull(by: By): WebElement? =
        try { findElement(by) } catch (e: NoSuchElementException) { null }
}