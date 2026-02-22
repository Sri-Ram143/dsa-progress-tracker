package tracker.storage;

import java.io.File;
import java.util.*;
import tracker.models.Problem;

public class ProblemRepository {
    private List<Problem>problems;
    public ProblemRepository(){
       problems=FileHandler.loadFromFile();
    }
    public boolean addProblem(Problem problem){
        for(Problem p:problems){
            if(p.getId()==problem.getId()){
                return false;
            }
        }
        problems.add(problem);
        FileHandler.saveToFile(problems);
        return true;
    }
    public List<Problem> getAllProblems(){
        return problems;
    }
    public boolean isEmpty(){
        return problems.isEmpty();
    }
    public Problem findById(int id){
        for(Problem i:problems){
            if(i.getId()==id){
                return i;
            }
        }
        return null;
    }
    public boolean deleteById(int id){
        for(int i=0;i<problems.size();i++){
            if(problems.get(i).getId()==id){
                problems.remove(i);
                FileHandler.saveToFile(problems);
                return true;
            }
        }
        FileHandler.saveToFile(problems);
        return false;
    }
}
