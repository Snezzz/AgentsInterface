import compromise.Agent;
import compromise.Algorithm;
import compromise.Compromise;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String [] args){
        Map<Integer,Integer> ratingList = new HashMap<Integer, Integer>();

        Agent agent1 = new Agent(1,"1","Вася", "Пупкин");
        ratingList.put(1,2);
        ratingList.put(2,6);
        ratingList.put(3,7);
        agent1.setRatingList(ratingList);

        ratingList = new HashMap<Integer, Integer>();
        Agent agent2 = new Agent(2,"2","Петя", "Стрелецкий");
        ratingList.put(1,1);
        ratingList.put(2,7);
        ratingList.put(3,2);
        agent2.setRatingList(ratingList);

        ratingList = new HashMap<Integer, Integer>();
        Agent agent3 = new Agent(3,"3","Иван", "Ургант");
        ratingList.put(1,3);
        ratingList.put(2,2);
        ratingList.put(3,9);
        agent3.setRatingList(ratingList);

        Algorithm algorithm1 = new Algorithm(1,"Ядра графа");
        Algorithm algorithm2 = new Algorithm(2,"Клики графа");
        Algorithm algorithm3 = new Algorithm(3,"Кластера графа");

        Agent [] agents = new Agent[3];
        agents[0] = agent1;
        agents[1] = agent2;
        agents[2] = agent3;

        Algorithm [] algorithms = new Algorithm[3];
        algorithms[0] = algorithm1;
        algorithms[1] = algorithm2;
        algorithms[2] = algorithm3;



    }
}
