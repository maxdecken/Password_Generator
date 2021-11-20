
public class Main {

        public static void main (String[] args){
            Engine e = new Engine();
            UserInterface u = new UserInterface(e);
            u.setVisible(true);
        }
}

