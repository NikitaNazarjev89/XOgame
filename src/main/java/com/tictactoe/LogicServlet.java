package com.tictactoe;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int index = getSelectedIndex(req);

        Field field = extractField(session);
        Sign currentSign = field.getField().get(index);

        if(currentSign != Sign.EMPTY){
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req,resp);
            return;
        }
        field.getField().put(index,Sign.CROSS);
        if(checkWin(resp,session,field)){
            return;
        }
        int indexEmptySign = field.getEmptyFieldIndex();

        if(indexEmptySign >=0){
            field.getField().put(indexEmptySign,Sign.NOUGHT);
            if (checkWin(resp, session, field)) {
                return;
            }
        }
        else {
            session.setAttribute("draw",true);
            List<Sign> dataIfDraw = field.getFieldData();
            session.setAttribute("data",dataIfDraw);
            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();
        session.setAttribute("field",field);
        session.setAttribute("data",data);
        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object field = currentSession.getAttribute("field");
        if (Field.class != field.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Illegal Argument Field in session");
        }
        return (Field) field;
    }


    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {

            session.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            session.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
