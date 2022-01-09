package com.mservicetech.campsite.repository;

import com.mservicetech.campsite.H2DatasourceStartupHook;
import com.mservicetech.campsite.model.Client;
import com.mservicetech.campsite.model.Reservation;
import com.networknt.service.SingletonServiceFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class CampsiteRepositoryTest {


    private static CampsiteRepository campsiteRepository = SingletonServiceFactory.getBean(CampsiteRepository.class);

    private static  Client client;
    private static Reservation reservation;
    private static H2DatasourceStartupHook h2DatasourceStartupHook;

    private static SqlSessionFactory sqlSessionFactory;


    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        client = new Client();
        client.setEmail("volcano.admin@gmail.com");
        reservation = new Reservation();
        reservation.setArrival(LocalDate.now());
        reservation.setDeparture(LocalDate.now().plusDays(3));
        h2DatasourceStartupHook= new H2DatasourceStartupHook();
        h2DatasourceStartupHook.onStartup();

        ApiSqlSessionFactoryBuilder apiSqlSessionFactoryBuilder = new ApiSqlSessionFactoryBuilder();
        sqlSessionFactory = apiSqlSessionFactoryBuilder.create();
    }

    @Test
    public void testFindReserved() throws SQLException {
        List<LocalDate> reservedList =  campsiteRepository.findReserved();
        assertTrue(reservedList.size()>0);
    }


    @Test
    public void testVerifyDates() throws SQLException {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(LocalDate.now());
        dateList.add(LocalDate.now().plusDays(1));
        dateList.add(LocalDate.now().plusDays(2));
        dateList.add(LocalDate.now().plusDays(3));
        SqlSession session = sqlSessionFactory.openSession();
        campsiteRepository.deleteDates(session, dateList);
        session.commit();
        List<LocalDate> result =  campsiteRepository.verifyDates(session, dateList);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testCheckUserExisting() throws SQLException{
        Client existing =  campsiteRepository.checkClientExisting(sqlSessionFactory.openSession(), client);
        assertNotNull(existing);
    }

    @Test
    public void testGetReservation() throws SQLException{
        Reservation existing =  campsiteRepository.getReservation("test1-1");
    //    assertNotNull(existing);
    }

    @Test
    public void testInsertClient() throws SQLException{
        Client client = new Client();
        client.setName("Test Test");
        client.setEmail("Test.Test@volcano.com");
        long newClient =  campsiteRepository.insertClient(sqlSessionFactory.openSession(), client);
        assertNotNull(newClient);
    }

    @Test
    public void testReservedDates() throws SQLException{
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(LocalDate.now());
        dateList.add(LocalDate.now().plusDays(1));
        SqlSession session = sqlSessionFactory.openSession();
        campsiteRepository.deleteDates(session, dateList);
        session.commit();
        int records =  campsiteRepository.reserveDates(session, dateList);
        session.commit();
        assertEquals(records, 2);
     //   records =  campsiteRepository.deleteDates(connection, dateList);
     //   assertEquals(records, 2);
    }

    @Test
    public void testCreateReservation() {
        Client client = new Client();
        client.setName("Test Test");
        client.setEmail("Test.Test2@volcano.com");
        reservation.setClient(client);
        String reservationId =  campsiteRepository.createReservation(reservation);
        assertNotNull(reservationId);
    }
}
