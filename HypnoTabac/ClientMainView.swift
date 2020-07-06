//
//  ContentView.swift
//  HypnoTabac
//
//  Created by Valentin on 26/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI
import Firebase

struct ClientMainView: View {
    @State var mainEnabled:Bool = true
    @State var gradeEnabled:Bool = false
    @State var conditionEnabled:Bool = false
    @State var navToStats:Bool = false
    @State var btn1Image:String = "btn1"
    @State var btn2Image:String = "btn2"
    @State var btn3Image:String = "btn3"
    @State private var grade:Int = 0
    @State private var condition:String = ""
    @State private var otherCondition:String = ""
    let conditionsList = ["Récompense/Motivation",
                          "Café",
                          "Alcool/Soirée",
                          "Après un repas",
                          "Pause",
                          "Avant le coucher",
                          "Voiture",
                          "Téléphone/Internet",
                          "Film"
    ]
    var usersReference: DatabaseReference = Database.database().reference(withPath: "users")
    
    var body: some View {
        NavigationView {
            ZStack{
                Rectangle().edgesIgnoringSafeArea(.all).foregroundColor(ColorManager.colorBackground)
                VStack{
                    VStack(){
                        Rectangle().foregroundColor(ColorManager.colorPrimary2).edgesIgnoringSafeArea(.all).frame(height: 80.0)
                        Spacer()
                    }.edgesIgnoringSafeArea(.all)
                }
                VStack{
                    ZStack(alignment:.topLeading){
                        StatusBarView()
                        if(!mainEnabled){
                            Button(action: {
                                if(self.gradeEnabled){
                                    self.mainEnabled.toggle()
                                    self.gradeEnabled.toggle()
                                } else if(self.conditionEnabled){
                                    self.gradeEnabled.toggle()
                                    self.conditionEnabled.toggle()
                                }
                            }){
                                Image(systemName: "chevron.left").frame(height: 80.0).padding([.leading],20).foregroundColor(ColorManager.colorBackground)
                            }
                        }
                    }
                    Spacer()
                    if mainEnabled {
                        VStack{
                            Spacer()
                            Button(action: {
                                //TODO
                                self.mainEnabled = false
                                self.gradeEnabled = true
                            }){
                                Text("AJOUTER UNE CIGARETTE").font(.system(size: 30)).bold().multilineTextAlignment(.center).foregroundColor(.white).padding(4).frame(width: 200, height:200).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2)
                            }
                            Spacer()
                            Button(action: {
                                self.mainEnabled = false
                                self.navToStats = true
                            }){
                                Text("VOIR LES STATISTIQUES").font(.system(size: 16)).bold().multilineTextAlignment(.center).foregroundColor(.white).padding(4).frame(width: 130, height:130).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2)
                            }
                            Spacer()
                        }
                    }
                    if gradeEnabled {
                        VStack{
                            Spacer()
                            HStack{
                                Text("A quel point cette cigarette est-elle indispensable?").padding().font(.title)
                                Spacer()
                            }
                            Spacer()
                            HStack{
                                Spacer()
                                Button(action: {
                                    if(self.btn1Image=="btn1"){
                                        self.btn1Image = "btn1_full"
                                        self.btn2Image = "btn2"
                                        self.btn3Image = "btn3"
                                        self.grade = 1
                                    } else {
                                        self.btn1Image = "btn1"
                                        self.grade = 0
                                    }
                                }){
                                    Image(btn1Image).renderingMode(.original).resizable().frame(width: 100, height: 100)
                                }
                                Spacer()
                                Button(action: {
                                    if(self.btn2Image=="btn2"){
                                        self.btn2Image = "btn2_full"
                                        self.btn1Image = "btn1"
                                        self.btn3Image = "btn3"
                                        self.grade = 2
                                    } else {
                                        self.btn2Image = "btn2"
                                        self.grade = 0
                                    }
                                }){
                                    Image(btn2Image).renderingMode(.original).resizable().frame(width: 100, height: 100)
                                }
                                Spacer()
                                Button(action: {
                                    if(self.btn3Image=="btn3"){
                                        self.btn3Image = "btn3_full"
                                        self.btn1Image = "btn1"
                                        self.btn2Image = "btn2"
                                        self.grade = 3
                                    } else {
                                        self.btn3Image = "btn3"
                                        self.grade = 0
                                    }
                                }){
                                    Image(btn3Image).renderingMode(.original).resizable().frame(width: 100, height: 100)
                                }
                                Spacer()
                            }
                            Spacer()
                            Button(action: {
                                //TODO
                                if(self.grade==0){
                                    
                                } else {
                                    self.gradeEnabled = false
                                    self.conditionEnabled = true
                                }
                            }){
                                Text("Suivant").bold().foregroundColor(.white).padding(8).frame(width: 300).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2)
                            }
                            Spacer()
                        }
                    }
                    if conditionEnabled {
                        VStack{
                            Spacer()
                            HStack{
                                Text("Dans quelles conditions consommez-vous cette cigarette?").padding().font(.title)
                                Spacer()
                            }
                            Spacer()
                            VStack {
                                HStack{
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[0]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[0]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[0], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[1]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[1]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[1], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[2]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[2]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[2], condition: self.$condition)
                                    }
                                    Spacer()
                                }
                                HStack{
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[3]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[3]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[3], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[4]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[4]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[4], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[5]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[5]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[5], condition: self.$condition)
                                    }
                                    Spacer()
                                }
                                HStack{
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[6]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[6]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[6], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[7]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[7]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[7], condition: self.$condition)
                                    }
                                    Spacer()
                                    Button(action: {
                                        if(self.condition==self.conditionsList[8]){
                                            self.condition = ""
                                        } else {
                                            self.condition = self.conditionsList[8]
                                        }
                                    }){
                                        ConditionButtonView(conditionText: conditionsList[8], condition: self.$condition)
                                    }
                                    Spacer()
                                }
                                HStack{
                                    Spacer()
                                    Button(action: {
                                        if(self.condition=="Autre"){
                                            self.condition = ""
                                        } else {
                                            self.condition = "Autre"
                                        }
                                    }){
                                        ConditionButtonView(conditionText: "Autre", condition: self.$condition)
                                    }
                                    Spacer()
                                }
                                if self.condition == "Autre" {
                                    TextField("Autre...", text: self.$otherCondition).padding(8).padding([.leading, .trailing], 4).frame(width: 350).background(LinearGradient(gradient: .init(colors: [ColorManager.colorBackground, .white]), startPoint: .top, endPoint: .bottom)).cornerRadius(20).shadow(color:.gray, radius: 2, x: 0, y: 2).padding()
                                }
                            }
                            Spacer()
                            Button(action: {
                                //TODO
                                print("Condition : \(self.condition)")
                                print("otherCondition : \(self.otherCondition)")
                                if(self.condition==""){
                                    
                                } else {
                                    if(self.condition=="Autre"){
                                        self.condition = self.otherCondition
                                    }
                                    
                                    if(UserDefaults.standard.object(forKey: "userID")==nil || UserDefaults.standard.object(forKey: "hypnoID")==nil){
                                        
                                    } else {
                                        let userID = UserDefaults.standard.string(forKey: "userID")
                                        let hypnoID = UserDefaults.standard.string(forKey: "hypnoID")
                                        
                                        let dbRef = self.usersReference.child(hypnoID ?? "").child("clients").child(userID ?? "").child("cigs")
                                        
                                        dbRef.observeSingleEvent(of: .value, with: { (snapshot) in
                                          // Get cig value
                                            let newIndex =  "\(snapshot.childrenCount)"
                                            
                                            dbRef.child(newIndex).child("grade").setValue(self.grade)
                                            dbRef.child(newIndex).child("condition").setValue(self.condition)
                                            
                                            let date = Date()
                                            let calendar = Calendar.current
                                            let year = calendar.component(.year, from: date)
                                            let month = calendar.component(.month, from: date)
                                            let day = calendar.component(.day, from: date)
                                            
                                            dbRef.child(newIndex).child("year").setValue(year)
                                            dbRef.child(newIndex).child("month").setValue(month)
                                            dbRef.child(newIndex).child("day").setValue(day)
                                            
                                          }) { (error) in
                                            print(error.localizedDescription)
                                        }
                                    }
                                    self.btn1Image = "btn1"
                                    self.btn2Image = "btn2"
                                    self.btn3Image = "btn3"
                                    self.conditionEnabled = false
                                    self.mainEnabled = true
                                }
                            }){
                                Text("Valider").bold().foregroundColor(.white).padding(8).frame(width: 300).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2)
                            }
                            Spacer()
                        }
                    }
                    NavigationLink(destination: ClientStatsView(statsEnabled: self.$navToStats, navToMain:self.$mainEnabled).navigationBarTitle("").navigationBarHidden(true), isActive: self.$navToStats){
                        EmptyView()
                    }.hidden()
                }
            }.navigationBarTitle("")
                .navigationBarHidden(true)
                .navigationBarBackButtonHidden(true)
        }
    }
}

struct ConditionButtonView: View {
    @State var conditionText:String
    @Binding var condition:String
    var body: some View {
        return ZStack{
            if(condition==conditionText){
                Text(conditionText).bold().foregroundColor(.white).padding(8).padding([.leading, .trailing],10).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).padding(16)
            } else {
                Text(conditionText).bold().foregroundColor(.white).padding(8).padding([.leading, .trailing],10).background(LinearGradient(gradient: .init(colors: [ColorManager.colorUnpressedBtn1, ColorManager.colorUnpressedBtn2]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).overlay(RoundedRectangle(cornerRadius: 50, style: .continuous).strokeBorder(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing), lineWidth: 4)).padding(16)
            }
        }
    }
}


struct ClientMainView_Previews: PreviewProvider {
    static var previews: some View {
        ClientMainView()
    }
}
