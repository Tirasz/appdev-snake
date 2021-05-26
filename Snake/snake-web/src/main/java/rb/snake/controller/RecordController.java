package rb.snake.controller;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import rb.snake.model.Record;
import rb.snake.dao.RecordDAO;
import java.util.List;
@WebServlet("/Record_Controller")
public class RecordController extends HttpServlet{
    public static final RecordDAO dao = new RecordDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Record r;
        try{
            //request parameters come from the record that was clicked on in the leaderboard
            String p1, p2;
            p1 = request.getParameter("p1");

            //init the record that was clicked on
            if(request.getParameter("type").equals("MP")){
                p2 = request.getParameter("p2");
                r = dao.getRecord(p1, p2);
                request.setAttribute("players", 2);
            }else{
                r = dao.getRecord(p1);
                request.setAttribute("players", 1);

            }

            //On DELETE, just delete the record and redirect to leaderboard.jsp
            if(request.getParameter("action").equals("DEL")){
                System.out.println("Trying to delete: "+ r);
                dao.delete(r);
                //response.sendRedirect("pages/leaderboard.jsp");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/pages/leaderboard.jsp");
                dispatcher.forward(request, response);
                return;
            }

            System.out.println("Trying to edit: " + r);
            request.setAttribute("record", r);


        }catch (Exception e){
            e.printStackTrace();
            response.sendRedirect("pages/leaderboard.jsp?error=3");
        }




    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        Record ogRecord; //The original record
        Record newRecord = new Record();

        try{
            //PLAYER 1
            String ogP1 = request.getParameter("ogP1");
            String newP1 = request.getParameter("p1");
            int newP1Score = Integer.parseInt(request.getParameter("p1Score"));
            newRecord.setP1(newP1);
            newRecord.setP1Score(newP1Score);

            //PLAYER 2
            String ogP2 = null;
            String newP2 = null;
            int newP2Score = -1;
            if(request.getParameterMap().containsKey("ogP2")){
                ogP2 = request.getParameter("ogP2");
                newP2 = request.getParameter("p2");
                newP2Score = Integer.parseInt(request.getParameter("p2Score"));
            }

            //getting the original record
            if(ogP2 != null){
                ogRecord = dao.getRecord(ogP1, ogP2);
                newRecord.setP2(newP2);
                newRecord.setP2Score(newP2Score);
            }else{
                ogRecord = dao.getRecord(ogP1);
            }


            //Checking new names, redirect with error if name is invalid
            Record.checkName(newP1);
            if(ogP2 != null){
                Record.checkName(newP2);
            }

            //update record, redirect back to leaderboard
            dao.update(ogRecord,newRecord);
            response.sendRedirect("pages/leaderboard.jsp");


        }catch (NumberFormatException e){
            //Should not happen, since input is of type number
            response.sendRedirect("pages/leaderboard.jsp?error=3");
        }catch (IllegalArgumentException e){
            //Happens when player names are too long/contain special chars
            if(e.getMessage().contains("special")){
                response.sendRedirect("pages/leaderboard.jsp?error=1");
            }else{
                response.sendRedirect("pages/leaderboard.jsp?error=2");
            }
        }catch (Exception e){
            //Should not happen but still
            response.sendRedirect("pages/leaderboard.jsp?error=4");
        }


    }


}
