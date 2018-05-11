  class copThread extends Thread {
  boolean running;
  int option;
  int r1,r2;
  copThread(){};
  copThread(int a){
  this.running = false;
  this.option = a;
  }
  copThread(int a1,int a2){
   this.r1 =a1;
   this.r2 = a2;
  }
    void start()
  {
    running = true;
    super.start();
  }
  
  void run(){
    while(running){
      computepartly(avoidWalls, seperate, align, cohere,r1,r2, type);
    }
  }
}
