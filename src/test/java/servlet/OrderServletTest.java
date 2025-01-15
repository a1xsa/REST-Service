package servlet;


import dto.OrderDTO;
import com.google.gson.Gson;
import exception.DatabaseException;
import exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.OrderService;
import servlet.OrderServlet;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServletTest {
    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private OrderService service;

    @InjectMocks
    private OrderServlet servlet;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        lenient().when(resp.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void getAllOrders_Success() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn(null);
        when(service.getAll()).thenReturn(List.of(new OrderDTO(1, "Product 1", 12.3, null)));

        servlet.service(req, resp);

        verify(resp).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Product 1"));
    }

    @Test
    void getAllOrder_NOT_FOUND() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getMethod()).thenReturn("GET");
        when(service.getAll()).thenThrow(new NotFoundException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "error");
    }

    @Test
    void getOrderById_Success() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("/1");
        when(service.getById(1)).thenReturn(List.of(new OrderDTO(1, "Product 1", 12.3, null)));

        servlet.service(req, resp);

        verify(resp).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Product 1"));
    }

    @Test
    void getOrderById_BAD_REQUEST() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/sadasdsad");
        when(req.getMethod()).thenReturn("GET");

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getOrderById_SERVER_ERROR() throws Exception {
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getMethod()).thenReturn("GET");
        when(service.getById(any())).thenThrow(new DatabaseException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
    }

    @Test
    void addOrder_Success() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String orderJson = new Gson().toJson(new OrderDTO(1, "Product 1", 12.3, null));
        when(req.getParameter("customer_id")).thenReturn("1");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(service.add(any(), eq(1))).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseWriter.toString().contains("Successful addition. Index assigned:1"));
    }

    @Test
    void addOrder_IdNull() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String orderJson = new Gson().toJson(new OrderDTO(1, "Product 1", 12.3, null));
        when(req.getParameter("customer_id")).thenReturn(null);

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required for add orders");
    }


    @Test
    void addOrder_BAD_REQUEST() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String orderJson = new Gson().toJson(new OrderDTO(1, "Product 1", 12.3, null));
        when(req.getParameter("customer_id")).thenReturn("100");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(service.add(any(), eq(100))).thenThrow(new NotFoundException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
    }


    @Test
    void deleteOrder_Success() throws Exception {
        when(req.getMethod()).thenReturn("DELETE");
        when(req.getPathInfo()).thenReturn("/1");
        when(service.deleteById(1)).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("Successful delete. Index deleted:1"));
    }

    @Test
    void deleteOrder_NOT_FOUND() throws Exception {
        when(req.getMethod()).thenReturn("DELETE");
        when(req.getPathInfo()).thenReturn("/111");
        when(service.deleteById(111)).thenThrow(new NotFoundException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND,"error");
    }


    @Test
    void updateOrder_Success() throws Exception {
        when(req.getMethod()).thenReturn("PUT");
        String orderJson = new Gson().toJson(new OrderDTO(1, "Product 1", 12.3, null));
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(service.updateOrder(eq(1), any())).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("Successful change. Index changed:1"));
    }

    @Test
    void updateOrder_BAD_REQUEST() throws Exception {
        when(req.getMethod()).thenReturn("PUT");
        String orderJson = new Gson().toJson(null);
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(service.updateOrder(1, null)).thenThrow(new IllegalArgumentException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
    }




}
