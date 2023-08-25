package lk.ijse.pos_app.servlet;

import lk.ijse.pos_app.dto.ItemDTO;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/pages/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajax", "root", "1234");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();

            resp.addHeader("Access-Control-Allow-Origin","*");

            JsonArrayBuilder allItems = Json.createArrayBuilder();
            ItemDTO itemDTO = new ItemDTO();
            while (resultSet.next()){
                itemDTO.setCode(resultSet.getString(1));
                itemDTO.setName(resultSet.getString(2));
                itemDTO.setQty(resultSet.getInt(3));
                itemDTO.setPrice(resultSet.getDouble(4));

                JsonObjectBuilder itemObject = Json.createObjectBuilder();
                itemObject.add("code",itemDTO.getCode());
                itemObject.add("name",itemDTO.getName());
                itemObject.add("qty",String.valueOf(itemDTO.getQty()));
                itemObject.add("price",String.valueOf(itemDTO.getPrice()));

                allItems.add(itemObject.build());
            }

            resp.getWriter().print(allItems.build());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
