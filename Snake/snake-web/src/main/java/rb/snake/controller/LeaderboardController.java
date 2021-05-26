package rb.snake.controller;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import rb.snake.model.Record;
import rb.snake.dao.RecordDAO;
import java.util.List;
import static rb.snake.controller.RecordController.dao;

@WebServlet("/Leaderboard_Controller")
public class LeaderboardController extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        List<Record> SP = dao.getAllSP();
        List<Record> MP = dao.getAllMP();

        if(request.getParameterMap().containsKey("error")){
            request.setAttribute("error", Integer.parseInt(request.getParameter("error")));
        }

        request.setAttribute("SPList", SP);
        request.setAttribute("MPList", MP);
    }



}

