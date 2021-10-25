class User {
  
  String name, surname, cf, dateBirth, role;

  User(this.name, this.surname, this.cf, this.dateBirth, this.role);
  
  User.fromJson(Map<String, dynamic> json)
      : this.name = json["name"],
        this.surname = json["surname"],
        this.dateBirth = json["dateBirth"],
        this.cf = json["cf"],
        this.role = json["role"];

}

