package model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String skills; // want to teach
    private String desiredSkill;// want to learn

    public User( int id,String name, String email, String password, String skills, String desiredSkill ){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.skills = skills;
        this.desiredSkill = desiredSkill;
        
        
    }

    // Getters and Setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }

    public String getName(){
         return name;
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getSkills() {
        return skills; 
    }
    public void setskill(String skills) { 
        this.skills = skills; 
    }
    public String getDesiredSkill() {
        return desiredSkill;
    }

    public void setDesiredSkill(String desiredSkill) {
        this.desiredSkill = desiredSkill;
    }
}
