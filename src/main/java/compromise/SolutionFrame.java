package compromise;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class SolutionFrame extends JFrame {

    Vector<Agent> agents;

   public SolutionFrame(Vector<Agent> agents){
       this.agents = agents;

       //Массив содержащий заголоки таблицы
       Object[] headers = { "Agent Name", "Optimal Algorithm" };

       Object [][] data = new Object[this.agents.size()][2];
       int i = 0;
       for (Agent agent: this.agents){
           data[i][0] = agent.getFirstName();
           data[i][1] = agent.getOptimalAlgorithm().getName();
            i++;
       }
       JTable table = new JTable(data,headers);
       JScrollPane jscrlp = new JScrollPane(table);
       table.setPreferredScrollableViewportSize(new Dimension(250, 100));


       JPanel panel = new JPanel();
       getContentPane().add(jscrlp);
       panel.add(table.getTableHeader(), BorderLayout.NORTH);
       panel.add(table);
       add(panel);
       setSize(200,200);
       setLocation(200,10);
       setVisible(true);
   }

}
