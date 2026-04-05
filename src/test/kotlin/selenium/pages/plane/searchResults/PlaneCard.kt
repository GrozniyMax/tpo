package selenium.pages.plane.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

data class PlaneCard(
    val airCompany: String,
    val flights: List<Flight>
)

data class Flight(
    val departure: Airport,
    val arrival: Airport,
    val duration: String
)

data class Airport(
    val code: String,
    val time: String,
    val day: String
)

object PlaneCardParser {

    fun parse(card: WebElement): PlaneCard {
        val airCompany = card.findElementOrNull(
            By.xpath(".//*[@data-testid='flight_card_carriers']")
        )?.text ?: ""

        val flights = parseFlights(card)

        return PlaneCard(
            airCompany = airCompany,
            flights = flights,
        )
    }

    private fun parseFlights(card: WebElement): List<Flight> {
        val flights = mutableListOf<Flight>()
        var segmentIndex = 0

        while (true) {
            val departureTimeEl = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_departure_time_$segmentIndex']//div")
            ) ?: break

            val departureAirport = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_departure_airport_$segmentIndex']")
            )?.text ?: ""

            val departureDate = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_departure_date_$segmentIndex']")
            )?.text ?: ""

            val arrivalTime = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_destination_time_$segmentIndex']//div")
            )?.text ?: ""

            val arrivalAirport = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_destination_airport_$segmentIndex']")
            )?.text ?: ""

            val arrivalDate = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_destination_date_$segmentIndex']")
            )?.text ?: ""

            val duration = card.findElementOrNull(
                By.xpath(".//*[@data-testid='flight_card_segment_duration_$segmentIndex']")
            )?.text ?: ""

            flights.add(
                Flight(
                    departure = Airport(
                        code = departureAirport,
                        time = departureTimeEl.text,
                        day = departureDate
                    ),
                    arrival = Airport(
                        code = arrivalAirport,
                        time = arrivalTime,
                        day = arrivalDate
                    ),
                    duration = duration
                )
            )

            segmentIndex++
        }

        return flights
    }

    private fun WebElement.findElementOrNull(by: By): WebElement? {
        return try {
            findElement(by)
        } catch (e: NoSuchElementException) {
            null
        }
    }
}