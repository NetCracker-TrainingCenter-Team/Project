
import controller.Serialize;
import model.Dish;

import java.io.*;
import java.net.Socket;

public class Client {
    private static DataInputStream dis;
    private Socket clientSocket;
    private DataOutputStream out;
    private final BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));


    public static void main(String[] args) throws IOException, InterruptedException {
       /* Client client = null;
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
        System.out.println("Завершение работы клиентской стороны...");*/
    }


    /**
     * Конструктор клиента
     */
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

    /**
     * Метод проверки на обновление файла
     * * @return значение истина или ложь
     * @throws IOException
     */
    public Boolean updateCheck() throws IOException {
        if (dis.readUTF().equals("Yes"))  return true;
        return false;
    }

    /**
     * Метод загрузки файла
     * @param nameFile - имя файла
     * * @return ответ сервера
     * @throws IOException
     */
    public String file(String nameFile) throws IOException {
        if (nameFile.equals("newData")){
            serialize(nameFile);
        }
        else out.writeUTF(nameFile);
        return dis.readUTF();
    }

    /**
     * Метод перезагрузки файла на сервере
     * @param newData - сообщение о перезагрузке
     * @param nameFile - имя файла
     * * @return ответ сервера
     * @throws IOException
     */
    public String file(String newData, String nameFile) throws IOException {
        out.writeUTF(newData);
        serialize(nameFile);
        return dis.readUTF();
    }

    /**
     * Метод отказа от перезагрузки на сервере
     * @throws IOException
     */
    public void file() throws IOException {
        out.writeUTF("");
    }

    /**
     * Метод ответа сервера
     * @return ответ сервера
     * @throws IOException
     */
    public String file0() throws IOException {
        return dis.readUTF();
    }

    /**
     * Метод сериализации
     */
    public void serialize(String nameFile){
        Serialize.serializeFile(nameFile,out);
    }

    /**
     * Метод добавления данных из файла
     * @param method - метод
     * @param nameFile - имя файла
     * @return ответ сервера
     * @throws IOException
     */
    public String addFile(String method, String nameFile) throws IOException {
        out.writeUTF(method);
        out.writeUTF(nameFile);
        return dis.readUTF();
    }

    /**
     * Метод печати данных
     * @param nameMethod - метод
     * @return ответ сервера
     * @throws IOException
     */
    public String print(String nameMethod) throws IOException {
        out.writeUTF(nameMethod);
        return dis.readUTF();
    }

    /**
     * Метод печати данных по категории
     * @param nameMethod - метод
     * @param data - категория
     * @return ответ сервера
     * @throws IOException
     */
    public String print(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
        return dis.readUTF();
    }

    /**
     * Метод сохранения файла
     * @param nameMethod - метод
     * @throws IOException
     */
    public void save(String nameMethod) throws IOException {
        out.writeUTF(nameMethod);
    }

    /**
     * Метод создания нового файла
     * @param nameMethod - метод
     * @throws IOException
     */
    public void newFile(String nameMethod) throws IOException {
        dis.readUTF();
        out.writeUTF(nameMethod);
    }

    /**
     * Метод сохранения файла как копию
     * @param nameMethod - метод
     * @param data - имя файла
     * @throws IOException
     */
    public void save(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
    }

    /**
     * Метод изменения данных
     * @param nameMethod - метод
     * @param data - данные
     * @throws IOException
     */
    public void setData(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
    }

    /**
     * Метод изменения данных
     * @param nameMethod - метод
     * @param data1 - данные
     * @param data1 - данные
     * @throws IOException
     */
    public void setData(String nameMethod, String data1, String data2) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data1);
        out.writeUTF(data2);
    }

    /**
     * Метод изменения цены
     * @param nameMethod - метод
     * @param data1 - данные
     * @param data1 - цена
     * @throws IOException
     */
    public void setData(String nameMethod, String data1, Double data2) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data1);
        out.writeDouble(data2);
    }

    /**
     * Метод добавления данных
     * @param nameMethod - метод
     * @param dish - блюдо
     * @throws IOException
     */
    public String addData(String nameMethod, Dish dish) throws IOException {
        out.writeUTF(nameMethod);
        Serialize.serializeDish(dish,out);
        return dis.readUTF();
    }

    /**
     * Метод добавления категории
     * @param nameMethod - метод
     * @param data - название категории
     * @throws IOException
     */
    public String addData(String nameMethod, String data) throws IOException {
        out.writeUTF(nameMethod);
        out.writeUTF(data);
        return dis.readUTF();
    }

    /**
     * Метод остановки сервера
     * @throws IOException
     */
    public void stop() throws IOException {
        dis.readUTF();
        out.writeUTF("Stop");
    }
}
