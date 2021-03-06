package org.thebreak.roombooking.app.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thebreak.roombooking.app.dao.BookingRepository;
import org.thebreak.roombooking.app.model.Booking;
import org.thebreak.roombooking.app.model.enums.BookingStatusEnum;
import org.thebreak.roombooking.app.service.BookingService;



import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingTimeOutCheck {

    @Autowired
    BookingService bookingService;
    @Autowired
    BookingRepository bookingRepository;

    @Scheduled(cron = "30 * * * * ?")
    public void checkAndCloseBooking() {

        // bookedAt > 30 min and status == unpaid; -> status set to closed; close reason: booking timeout;
        List<Booking> bookingList = bookingRepository.findByStatus(BookingStatusEnum.UNPAID.getCode());

        if (bookingList.size() == 0) return;

        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookingList) {
            // close bookings older than 30 mins
            if (booking.getCreatedAt().isBefore(now.minusMinutes(30))) {
                bookingService.updateStatusById(booking.getId(), BookingStatusEnum.CLOSED.getCode(), null);
                System.out.println("checkAndCloseBooking: set status to closed");
            }
        }
    }
}
