import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class Task{
  String description,status;
  LocalDateTime createdAt,updatedAt;
  Task(String description,String status){
    this.description=description;
    this.status=status;
    this.createdAt=LocalDateTime.now();
    this.updatedAt=LocalDateTime.now();
  }
  Task(String description,String status, LocalDateTime createdAt, LocalDateTime updatedAt){
    this.description=description;
    this.status=status;
    this.createdAt=createdAt;
    this.updatedAt=updatedAt;
  }
  @Override 
  public String toString(){
    DateTimeFormatter fmt=DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    return String.format("description=%s\nstatus=%s\ncreated-at=%s\nupdated-at=%s\n",
        this.description,this.status,this.createdAt.format(fmt),this.updatedAt.format(fmt));
  }
}

class TaskList{
  HashMap<Integer,Task> taskList;
  TaskList(){
    this.taskList=new HashMap<>();
  }
  public void addTask(int id, Task task){
    taskList.put(id, task);
  }
  public int addTask(String desc){
    Task task=new Task(desc,"todo");
    taskList.put(taskList.size()+1, task);
    return taskList.size();
  }
  public void updateTask(int id,String newDesc){
    if(!taskList.containsKey(id))
      System.out.println("Task not found");
    Task task=taskList.remove(id);
    task.description=newDesc;
    task.updatedAt=LocalDateTime.now();
    taskList.put(id, task);
    System.out.println("Task updated!");
  }
  public void deleteTask(int id){
    if(!taskList.containsKey(id))
      System.out.println("Task not found");
    taskList.remove(id);
    System.out.println("Task deleted!");
  }
  public void listItems(){
    if(taskList.isEmpty()) System.out.println(0);
    taskList.forEach((id,task)->{
      System.out.format("id=%d\n%s\n",id,task);
    });
  }
  public void listToDo(){
    taskList.forEach((id,task)->{
      if(task.status.equals("todo"))
        System.out.format("id=%d\n%s\n",id,task);
    });
  }
  public void listDone(){
    taskList.forEach((id,task)->{
      if(task.status.equals("done"))
        System.out.format("id=%d\n%s\n",id,task);
    });
  }
  public void listInProgress(){
    taskList.forEach((id,task)->{
      if(task.status.equals("in-progress"))
        System.out.format("id=%d\n%s\n",id,task);
    });
  }
  public void markInProgress(int id){
    if(!taskList.containsKey(id))
      System.out.println("Task not found!");
    Task task=taskList.remove(id);
    task.status="in-progress";
    task.updatedAt=LocalDateTime.now();
    taskList.put(id, task);
    System.out.println("Task moved to In-Progress!");
  }
  public void markDone(int id){
    if(!taskList.containsKey(id)) 
      System.out.println("Task not found!");
    Task task=taskList.remove(id);
    task.status="done";
    task.updatedAt=LocalDateTime.now();
    taskList.put(id, task);
    System.out.println("Task moved to Done!");
  }
  public int itemCount(){
    return taskList.size();
  }
}

class TaskTracker{
  static void executeTask(TaskList tasklist, String[] cmdList){
    String task="";
    switch(cmdList[1]){
      case "add":
        task=String.join(" ",Arrays.asList(cmdList).subList(2, cmdList.length)); 
        System.out.println(tasklist.addTask(task)); 
        break;
      case "update": 
        task=String.join(" ",Arrays.asList(cmdList).subList(3, cmdList.length)); 
        tasklist.updateTask(Integer.parseInt(cmdList[2]), task); 
        break;
      case "delete": 
        tasklist.deleteTask(Integer.parseInt(cmdList[2])); 
        break;
      case "list":
        if(cmdList.length==2)
          tasklist.listItems();
        else if(cmdList[2].equals("todo"))
          tasklist.listToDo();
        else if(cmdList[2].equals("in-progress"))
          tasklist.listInProgress();
        else if(cmdList[2].equals("done"))
          tasklist.listDone();
        else
          System.out.println("Incorrect command!");
        break;
      case "mark-in-progress":
        tasklist.markInProgress(Integer.parseInt(cmdList[2]));
        break;
      case "mark-done":
        tasklist.markDone(Integer.parseInt(cmdList[2]));
        break;
      default: 
        System.out.println("Incorrect command!");
    }
  }
  public static void main(String[] args) throws Exception{
    boolean cmdLoop=true;
    TaskList taskList=new TaskList();
    Scanner sc=new Scanner(System.in);
    List<String> data=Files.readAllLines(Paths.get("tasks.txt"));
    data.forEach(line->{
      String[] entries=line.split(",");
      Task task=new Task(entries[1],entries[2],
        LocalDateTime.parse(entries[3]),LocalDateTime.parse(entries[4]));
      taskList.addTask(Integer.parseInt(entries[0]),task);
    });
    while(cmdLoop){
      System.out.print("> ");
      String command=sc.nextLine();
      String[] cmdList=command.split(" ");
      if(cmdList[0].equals("exit"))
        cmdLoop=false;
      else{
        switch(cmdList[0]){
          case "task-cli": executeTask(taskList,cmdList); break;
          default: System.out.println("Incorrect command!");
        }
      }
    }
    if(taskList.itemCount()!=0){
      data.clear();
      taskList.taskList.forEach((id,task)->{
        data.addLast(String.format("%d,%s,%s,%s,%s", 
          id,task.description,task.status,task.createdAt,task.updatedAt
        ));
      });
      Files.write(Paths.get("tasks.txt"), data);
    }
    sc.close();
  }
}