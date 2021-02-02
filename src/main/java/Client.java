import java.io.*;
import java.net.Socket;

public class Client {
    private static DataInputStream dis;
    private Socket clientSocket;
    private DataOutputStream out;
    private final BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));


    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = null;
        try {
            client = new Client(8000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(client!=null) {
            System.out.println("Введите команду: ");
            String clientCommand = client.reader.readLine();
            client.out.writeUTF(clientCommand);
            client.out.flush();
            System.out.println("Отправка команды \"" + clientCommand + "\" на сервер...");
            Thread.sleep(1000);
            System.out.println("Получаем ответ от сервера...");
            String in = client.dis.readUTF();
            System.out.println("Ответ с сервера: " + in);

        }
        System.out.println("Завершение работы клиентской стороны...");
    }


    public Client(int socketPort) {
        try {
            String socketHost = "localhost";
            clientSocket = new Socket(socketHost, socketPort);
            dis = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Клиент успешно создан на порту " + socketPort);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Ошибка при создании клиента!");
        }
    }


    public Boolean updateCheck() throws IOException {
        if (dis.readUTF().equals("Yes"))  return true;
        return false;
    }

    public String file(String nameFile) throws IOException {
       if (nameFile.equals("newData")){
           serialize(nameFile);
       }
        else out.writeUTF(nameFile);
       return dis.readUTF();
    }

    public String file() throws IOException {
        return dis.readUTF();
    }



    public void serialize(String nameFile){
        Serialize.deserializeFile(nameFile,dis);

    }

    public String addFile(String method, String nameFile) throws IOException {
        out.writeUTF(method);
        out.writeUTF(nameFile);
        return dis.readUTF();
    }

    public String print(String nameMethod) throws IOException {
        out.writeUTF(nameMethod);
        return dis.readUTF();
    }

    public String print(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
        return dis.readUTF();
    }

    public void save(String nameMethod) throws IOException {
        out.writeUTF(nameMethod);
    }

    public void newFile(String nameMethod) throws IOException {
        dis.readUTF();
        out.writeUTF(nameMethod);
    }

    public void save(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
    }


    public void setData(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
    }

    public void setData(String nameMethod, String data1, String data2) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data1);
        out.writeUTF(data2);
    }

    public void setData(String nameMethod, String data1, Double data2) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data1);
        out.writeDouble(data2);
    }

    public String addData(String nameMethod, Dish dish) throws IOException {
        out.writeUTF(nameMethod);
        Serialize.serializeDish(dish,out);
        return dis.readUTF();
    }

    public String addData(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
        return dis.readUTF();
    }

    public void stop() throws IOException {
        dis.readUTF();
        out.writeUTF("Stop");
    }
}