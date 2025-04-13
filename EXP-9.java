//Easy Level – Spring Dependency Injection using Java Config

//Course.java
public class Course {
    private String courseName;
    private int duration;

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() { return courseName; }
    public int getDuration() { return duration; }
}
//Student.java
public class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public void display() {
        System.out.println("Student Name: " + name);
        System.out.println("Course: " + course.getCourseName() + ", Duration: " + course.getDuration() + " months");
    }
}
//AppConfig.java
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {
    @Bean
    public Course course() {
        return new Course("Java Programming", 3);
    }

    @Bean
    public Student student() {
        return new Student("Alice", course());
    }
}
//MainApp.java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        student.display();
    }
}
//OUTPUT
Student Name: Alice  
Course: Java Programming, Duration: 3 months

  //Medium Level – Hibernate CRUD on Student Entity
  //Student.java (Entity)
  import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student {
    @Id
    private int id;
    private String name;
    private int age;

    public Student() {}
    public Student(int id, String name, int age) {
        this.id = id; this.name = name; this.age = age;
    }

    // Getters and setters omitted for brevity
}
//hibernate.cfg.xml
<hibernate-configuration>
  <session-factory>
    <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
    <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/testdb</property>
    <property name="hibernate.connection.username">root</property>
    <property name="hibernate.connection.password">password</property>
    <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
    <property name="hibernate.hbm2ddl.auto">update</property>
    <mapping class="Student"/>
  </session-factory>
</hibernate-configuration>

  //StudentCRUD.java
  import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class StudentCRUD {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        // Create
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(new Student(1, "Bob", 22));
        session.getTransaction().commit();
        session.close();

        // Read
        session = factory.openSession();
        Student s = session.get(Student.class, 1);
        System.out.println("Read: " + s.getName());
        session.close();

        // Update
        session = factory.openSession();
        session.beginTransaction();
        s.setAge(23);
        session.update(s);
        session.getTransaction().commit();
        session.close();

        // Delete
        session = factory.openSession();
        session.beginTransaction();
        session.delete(s);
        session.getTransaction().commit();
        session.close();

        factory.close();
    }
}
//OUTPUT
Read: Bob

  //Hard Level – Spring + Hibernate + Transaction Management (Bank Transfer)
 //Account.java (Entity)

  import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    private int accountId;
    private String name;
    private double balance;

    public Account() {}
    public Account(int accountId, String name, double balance) {
        this.accountId = accountId; this.name = name; this.balance = balance;
    }

    // Getters and setters omitted
}
//BankService.java (Transactional Service)
import jakarta.transaction.Transactional;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void transfer(int fromId, int toId, double amount) {
        Session session = sessionFactory.getCurrentSession();

        Account from = session.get(Account.class, fromId);
        Account to = session.get(Account.class, toId);

        if (from.getBalance() < amount) throw new RuntimeException("Insufficient funds");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }
}
//AppConfig.java
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "")
public class AppConfig {
    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration()
                .configure() // hibernate.cfg.xml
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();
    }
}
//BankMain.java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BankMain {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        BankService bank = context.getBean(BankService.class);

        try {
            bank.transfer(1, 2, 500.0);
            System.out.println("Transfer successful!");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        context.close();
    }
}
//OUTPUT
//SUCCESS:
Transfer successful!
//FAILURE: 
Transfer failed: Insufficient funds
