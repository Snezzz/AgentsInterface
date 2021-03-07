package compromise;

import java.util.*;

public class Compromise {
    private Map<Integer, Map<Integer, Integer>> H;
    private int n;
    private int m;
    private List<List<Integer>> X; //список id алгоритма
    private List<Object []>obj;
    private List<List<Integer>> gainMatrix;
    private Map<Integer, Integer> M; //key = agent id, value = max value
    private List<List<Integer>> diff; //key = agent id, value = max value
    private Vector <Agent> agents;
    private Vector <Algorithm> algorithms;

    public Compromise(int agentsCount, int algorithmsCount){
        this.n = agentsCount;
        this.m = algorithmsCount;
        this.H = new HashMap<Integer, Map<Integer, Integer>>();
        this.X = new ArrayList<List<Integer>>();
        this.gainMatrix = new ArrayList<List<Integer>>();
        this.M = new HashMap<Integer, Integer>();
        this.diff = new ArrayList<List<Integer>>();
        this.obj = new ArrayList<Object[]>();
    }

/** Создание матрицы полезности H
 * i - агент
 * j - алгоритм
 * H[i][j] - оценка (0 - 5)
 * */
    public void fillH(Vector<Agent> agents, Vector<Algorithm> algorithms){
        int i = 0;

        this.agents = agents;
        this.algorithms = algorithms;

        for (Agent agent: agents){
            Map <Integer, Integer> values = new HashMap<Integer, Integer>();
            for (Algorithm algorithm: algorithms){
                Integer rating = agent.getRatingList().get(algorithm.getId());
                if (rating == null)
                    values.put(algorithm.getId(),0);
                else
                    values.put(algorithm.getId(),rating);
            }
            setHValue(agent.getId(),values);

        }

    }

    private void setHValue(int agentId, Map <Integer, Integer> values){
        this.H.put(agentId,values);
    }


    //
    public void getSolutions(){
        List<Integer> algorithms = new ArrayList<Integer>();
        for (Algorithm algorithm: this.algorithms){
            algorithms.add(algorithm.getId());
        }
        permute(this.algorithms, this.n);
        getSolution();
    }

    private void getSolution(){
        fillGainMatrix(this.X);
        findM(this.gainMatrix);
        getDifference(this.gainMatrix);
        findSolution();
    }


    void permute(List<Algorithm> a, int k) {
        int n = a.size();

        int[] indexes = new int[k];
        int total = (int) Math.pow(n, k);


        while (total-- > 0) {
            List<Integer> snapshot = new ArrayList<Integer>();
            for (int i = 0; i < k; i++){
                snapshot.add(i,a.get(indexes[i]).getId());
            }
            //
            this.X.add(snapshot);

            for (int i = 0; i < k; i++) {
                if (indexes[i] >= n - 1) {
                    indexes[i] = 0;
                } else {
                    indexes[i]++;
                    break;
                }
            }
        }
    }


    private void permuteIteration(List<Integer> arr, int pos,int maxUsed, int limit) {
        //последняя итерация
        if (pos == limit) {
            List<Integer> copy = new ArrayList<Integer>(arr);
            this.X.add(copy);
            return;
        }

        for (int i = maxUsed; i < n; i++) {
            Integer temp = arr.get(pos);
            arr.set(pos, arr.get(i));
            arr.set(i, temp);

            permuteIteration(arr, pos + 1, i, limit);

            temp = arr.get(pos);
            arr.set(pos, arr.get(i));
            arr.set(i, temp);

        }
    }

    /**
     * Строим матрицу выигрышей
     * @param X - всевозможные комбинации
     */
    private void fillGainMatrix(List<List<Integer>> X){
        this.gainMatrix = new ArrayList<>();
        for (List<Integer> x: X){
            List<Integer> ranges = new ArrayList<Integer>();
            int i = 0;
            for (Integer algorithmId: x){
                Integer agentId = agents.get(i).getId();
                ranges.add(this.H.get(agentId).get(algorithmId));
                i++;
            }
            this.gainMatrix.add(ranges);
        }

    }

    /**
     * Поиск идеального вектора
     * @param gainMatrix
     */
    private void findM(List<List<Integer>> gainMatrix){
        this.M = new HashMap<Integer, Integer>();
        int k = gainMatrix.get(0).size();
        //Integer [][] matrix = (Integer[][]) gainMatrix.toArray();
        for(int j = 0; j < gainMatrix.get(0).size(); j++){
            int maxValue = 0;
            for (int i = 0; i < gainMatrix.size(); i++){
                int value = gainMatrix.get(i).get(j);
                if (value > maxValue)
                    maxValue = value;
            }
            this.M.put(j, maxValue);
        }
    }

    private void getDifference(List<List<Integer>> gainMatrix){
        this.diff =  new ArrayList<List<Integer>>();
        for(int j = 0; j < gainMatrix.get(0).size(); j++){
            int m = this.M.get(j);
            List<Integer> diff = new ArrayList<Integer>();
            for (int i = 0; i < gainMatrix.size(); i++){
                int value = m - gainMatrix.get(i).get(j);
                diff.add(value);
            }
            this.diff.add(diff);
        }
    }
    private void findSolution(){
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(); //key = номер сочетания, value = макс значение отклонения
        int i = 0;
        for(List<Integer> x: this.X){
            int max = 0;

            for (List<Integer> values: this.diff){
                    int diff = values.get(i);
                    if (diff > max) {
                        max = diff;
                    }
                }
            map.put(i,max);
            i++;

            }


        List<Integer> minValues = new ArrayList<Integer>();
        int min = 10000;
        int minId = -1; //id сочетания
        for(Map.Entry<Integer,Integer> entry: map.entrySet()){
            int value = entry.getValue();
            if (value < min) {
                min = value;
                minId = entry.getKey();
            }
        }

        //список оптимальных комбинаций
        for(Map.Entry<Integer,Integer> entry: map.entrySet()){
            int value = entry.getValue();
            if (value == min)
                minValues.add(entry.getKey());
        }

        //одно решение или же разности одинаковые
        if ((minValues.size() == 1)||(minValues.size() == map.size())) {
            //ставим каждому агенту в соответствие оптимальное решение
            List<Integer> optimalVector = this.X.get(minId);
            i = 0;
            for (Integer algorithmId : optimalVector) {
                Algorithm algorithm = findAlgorithmById(algorithmId);
                this.agents.get(i).setOptimalAlgorithm(algorithm);
                i++;
            }
        }
        //несколько решений
        else{
            List<List<Integer>> newList = new ArrayList<List<Integer>>();
            for (Integer xId: minValues){
                newList.add(this.X.get(xId));
            }
            this.X = newList;
            getSolution();
        }

    }

    private Algorithm findAlgorithmById(int id){
        for (int j = 0; j < algorithms.size(); j++){
            if (algorithms.get(j).getId() == id)
                return algorithms.get(j);
        }
        return null;
    }
}
