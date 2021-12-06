package com.theappschef.jktrc;

public class ClientModel {

    private String client_id,name,email,phone,company,work_area,material,specified,sample;

    public String getClient_id() {
        return client_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getCompany(){
        return company;
    }
    public void setCompany(String company){
        this.company=company;
    }
    public String getWork_area(){
        return work_area;
    }
    public void setWork_area(String work_area){
        this.work_area=work_area;
    }
    public String getMaterial(){
        return material;
    }
    public void setMaterial(String material){
        this.material=material;
    }
    public String getSpecified(){
        return  specified;
    }
    public void setSpecified(String specified){
        this.specified=specified;
    }
    public String getSample(){
        return sample;
    }
    public void setSample(String sample){
        this.sample=sample;
    }
}
