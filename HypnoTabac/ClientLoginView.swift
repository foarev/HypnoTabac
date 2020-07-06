//
//  WelcomeView.swift
//  HypnoTabac
//
//  Created by Valentin on 29/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI
import Firebase

struct ClientLoginView: View {
    var link:String
    @Binding var email:String
    @State var navToMain:Bool = false
    var body: some View {
        ZStack{
            Rectangle().edgesIgnoringSafeArea(.all).foregroundColor(ColorManager.colorBackground)
            VStack{
                VStack(){
                    Rectangle().foregroundColor(ColorManager.colorPrimary2).edgesIgnoringSafeArea(.all).frame(height: 80.0)
                    Spacer()
                }.edgesIgnoringSafeArea(.all)
            }
            VStack{
                WelcomeStatusBarView()
                HStack{
                    Text("Confirmez votre adresse email pour continuer").padding().font(.title)
                    Spacer()
                }
                TextField("Confirmer votre email", text: $email).foregroundColor(.white).padding(8).padding([.leading, .trailing], 4).frame(width: 350).background(LinearGradient(gradient: .init(colors: [ColorManager.colorBackground, .white]), startPoint: .top, endPoint: .bottom)).cornerRadius(20).shadow(color:.gray, radius: 2, x: 0, y: 2).padding()
                Button(action: {
                    //TODO
                    if true {//Auth.auth().isSignIn(withEmailLink: self.link) {
                        /*Auth.auth().signIn(withEmail: self.email, link: self.link) { (user, error) in
                                if(error==nil){*/
                                    //TODO: GET USER ID FROM FIREBASE
                        //                                    UserDefaults.standard.set(Auth.auth().currentUser?.uid, forKey: "userID")
                                    UserDefaults.standard.set("fwNUJvVpjydKFkLgmKrLPuWmML73", forKey: "userID")
                                    //TODO: GET HYPNO ID FROM FIREBASE AND TRANSFER DATA
                                    UserDefaults.standard.set("idPQMklHdhT61BXlwKrnsWGWzxk2", forKey: "hypnoID")
                                    self.navToMain = true
                                //}
                            //}
                    }

                }){
                    Text("Connexion").bold().foregroundColor(.white).padding(8).frame(width: 300).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2).padding()
                }
                Spacer()
            }
            if(self.navToMain){
                ClientMainView()
            }
        }
    }
}

struct ClientLoginView_Previews: PreviewProvider {
    static var previews: some View {
        ClientLoginView(link:"", email: .constant(""))
    }
}
