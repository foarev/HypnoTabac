//
//  ContentView.swift
//  HypnoTabac
//
//  Created by Valentin on 26/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI
import Firebase

struct ContentView: View {
    var link:String
    @State var navToWelcome:Bool = false
    @State var navToMain:Bool = false
    @State var loading:Bool = true
    
    var body: some View {
        NavigationView{
            ZStack{
                Rectangle().edgesIgnoringSafeArea(.all).foregroundColor(ColorManager.colorBackground)
                VStack{
                    VStack(){
                        Rectangle().foregroundColor(ColorManager.colorPrimary2).edgesIgnoringSafeArea(.all).frame(height: 80.0)
                        Spacer()
                    }.edgesIgnoringSafeArea(.all)
                }
                VStack{
                    StatusBarView()
                    Spacer()
                }
                if(self.link != "") {
                    ClientLoginView(link: link, email: .constant(""))
                } else if(navToMain){
                    ClientMainView()
                } else if(navToWelcome){
                    WelcomeView()
                } else if(self.loading) {
                    Loader()
                }
            }.navigationBarTitle("")
            .navigationBarHidden(true)
            .navigationBarBackButtonHidden(true)
            .onAppear{
                if(self.link != "") {
                    self.loading = false
                } else if(UserDefaults.standard.string(forKey: "userType") == "hypno" && UserDefaults.standard.string(forKey: "email") != ""){
                    // Go to hypno main
                    self.navToWelcome = true
                    self.loading = false
                } else if(UserDefaults.standard.string(forKey: "userType") == "client" && UserDefaults.standard.string(forKey: "email") != ""){
                    let hypnoID = UserDefaults.standard.string(forKey: "hypnoID")
                    let userID = UserDefaults.standard.string(forKey: "userID")
                    Database.database().reference(withPath: "users").child(hypnoID ?? "").child("clients").child(userID ?? "").observeSingleEvent(of: .value, with: { (snapshot) in
                        if(snapshot.value != nil){
                            self.navToMain = true
                            self.loading = false
                        } else {
                            self.navToWelcome = true
                            self.loading = false
                        }
                    })
                } else {
                    self.navToWelcome = true
                    self.loading = false
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(link:"")
    }
}
