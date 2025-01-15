package servlet;


import dto.CustomerDTO;
import com.google.gson.Gson;
import exception.DatabaseException;
import exception.DuplicateDataException;
import exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CustomerService;
import servlet.CustomerServlet;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServletTest {
    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerServlet servlet;

    private StringWriter responseWriter;


    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        lenient().when(resp.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void getAllCustomer_Success() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getMethod()).thenReturn("GET");
        when(service.getAll()).thenReturn(List.of(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com")));

        servlet.service(req, resp);

        verify(resp).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Tsvetkov Alexey"));
    }

    @Test
    void getAllCustomer_NOT_FOUND() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getMethod()).thenReturn("GET");
        when(service.getAll()).thenThrow(new NotFoundException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "error");
    }

    @Test
    void getAllCustomer_SERVER_ERROR() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getMethod()).thenReturn("GET");
        when(service.getAll()).thenThrow(new DatabaseException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
    }


    @Test
    void getCustomerById_Success() throws Exception {
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getMethod()).thenReturn("GET");
        when(service.getById(any())).thenReturn(List.of(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com")));

        servlet.service(req, resp);

        verify(resp).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Tsvetkov Alexey"));
    }

    @Test
    void getCustomerById_SERVER_ERROR() throws Exception {
        when(req.getPathInfo()).thenReturn("/1");
        when(req.getMethod()).thenReturn("GET");
        when(service.getById(any())).thenThrow(new DatabaseException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
    }

    @Test
    void addCustomer_Success() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String customerJson = new Gson().toJson(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com"));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(customerJson)));
        when(service.add(any())).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseWriter.toString().contains("Successful addition. Index assigned:1"));
    }

    @Test
    void addCustomer_SERVER_ERROR() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String customerJson = new Gson().toJson(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com"));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(customerJson)));
        when(service.add(any())).thenThrow(new DatabaseException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error");
    }


    @Test
    void addCustomer_Duplicate() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        String customerJson = new Gson().toJson(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com"));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(customerJson)));
        when(service.add(any())).thenThrow(new DuplicateDataException("error"));

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        when(req.getMethod()).thenReturn("DELETE");
        when(req.getPathInfo()).thenReturn("/1");
        when(service.deleteById(1)).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("Successful delete. Index deleted:1"));
    }

    @Test
    void deleteCustomer_BAD_REQUEST() throws Exception {
        when(req.getMethod()).thenReturn("DELETE");
        when(req.getPathInfo()).thenReturn("/");

        servlet.service(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required for deletion");
    }

    @Test
    void updateCustomer_Success() throws Exception {
        when(req.getMethod()).thenReturn("PUT");
        String customerJson = new Gson().toJson(new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com"));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(customerJson)));
        when(req.getPathInfo()).thenReturn("/1");
        when(service.updateCustomer(eq(1), any())).thenReturn(1);

        servlet.service(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("Successful change. Index changed:1"));
    }


}
