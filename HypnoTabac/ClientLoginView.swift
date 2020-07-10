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
    @State var loading:Bool = true
    let usersReference: DatabaseReference = Database.database().reference(withPath: "users")
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
                if(self.loading) {
                    Loader()
                }
                Button(action: {
                    self.usersReference.observeSingleEvent(of: .value, with: { (snapshot) in
                        if(snapshot.value != nil){
                            let hypnos = snapshot.value as! NSDictionary
                            var isEmailRegistered = false
                            var randomID = ""
                            var firstName = ""
                            var lastName = ""
                            var hypnoID = ""
                            var responses:[String] = []
                            
                            hypnos.forEach {h in
                                let hypno = h.value as! NSDictionary
                                if(hypno["clients"] != nil){
                                    (hypno["clients"] as! NSDictionary).forEach{c in
                                        let singleClient = c.value as! NSDictionary
                                        let singleEmail = singleClient["email"] as! String
                                        if(singleEmail == self.email){
                                            isEmailRegistered = true
                                            randomID = c.key as! String
                                            hypnoID = h.key as! String
                                            firstName = singleClient["firstName"] as! String
                                            lastName = singleClient["lastName"] as! String
                                            responses = singleClient["responses"] as! [String]
                                            return
                                        }
                                    }
                                }
                            }
                            if(!isEmailRegistered){
                                self.loading = false
                                return
                            } else {
                                if Auth.auth().isSignIn(withEmailLink: self.link) {
                                    Auth.auth().signIn(withEmail: self.email, link: self.link) { (user, error) in
                                        if(error==nil){
                                            let userID = Auth.auth().currentUser?.uid
                                            
                                            // Remove random ID
                                            self.usersReference.child(hypnoID).child("clients").child(randomID).removeValue()
                                            let dbCurrentUser = self.usersReference.child(hypnoID).child("clients").child(userID!)
                                            
                                            dbCurrentUser.child("email").setValue(self.email)
                                            dbCurrentUser.child("hypnoID").setValue(hypnoID)
                                            dbCurrentUser.child("firstName").setValue(firstName)
                                            dbCurrentUser.child("lastName").setValue(lastName)
                                            var i = 0
                                            responses.forEach{ r in
                                                dbCurrentUser.child("responses").child("\(i)").setValue(r)
                                                i += 1
                                            }
                                            UserDefaults.standard.set(self.email, forKey: "email")
                                            UserDefaults.standard.set("client", forKey: "userType")
                                            UserDefaults.standard.set(userID, forKey: "userID")
                                            UserDefaults.standard.set(hypnoID, forKey: "hypnoID")
                                            self.loading = false
                                            //UserDefaults.standard.set("fwNUJvVpjydKFkLgmKrLPuWmML73", forKey: "userID")
                                            //TODO: GET HYPNO ID FROM FIREBASE AND TRANSFER DATA
                                            //UserDefaults.standard.set("idPQMklHdhT61BXlwKrnsWGWzxk2", forKey: "hypnoID")
                                            self.navToMain = true
                                        }
                                    }
                                }
                            }
                        }
                    })
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
