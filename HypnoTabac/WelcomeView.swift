//
//  WelcomeView.swift
//  HypnoTabac
//
//  Created by Valentin on 29/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct WelcomeView: View {
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
                Spacer()
                VStack{
                    HStack{
                        Text(verbatim: "Si vous êtes un client").foregroundColor(ColorManager.colorPrimary).shadow(color: ColorManager.colorShadow, radius: 2, x: 1, y: 3).padding().font(.title)
                        Spacer()
                    }
                    HStack{
                        Text(verbatim: "Veuillez vous connecter sur l'application à l'aide du lien reçu par email. Si vous n'avez rien reçu, vérifiez vos spams ou contactez votre thérapeute.").padding()
                        Spacer()
                    }
                }
                VStack{
                    HStack{
                        Text(verbatim: "Si vous êtes un hypnothérapeute").foregroundColor(ColorManager.colorPrimary).shadow(color: ColorManager.colorShadow, radius: 2, x: 1, y: 3).padding().font(.title)
                        Spacer()
                    }
                    HStack{
                        Text(verbatim: "Cette fonctionnalité n'est pour l'instant disponible que sur Android.").padding()
//                        Text(verbatim: "Veuillez vous connecter ou créer un compte ci-dessous. Les comptes hypnothérapeute sont payants.").padding()
                        Spacer()
                    }
                }
                Spacer()
//                VStack{
//                    Button(action: {
//                        //TODO
//                    }){
//                        Text("Connexion").bold().foregroundColor(.white).padding(8).frame(width: 300).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimaryLight, ColorManager.colorPrimary]), startPoint: .leading, endPoint: .trailing)).cornerRadius(50).shadow(color:.gray, radius: 2, x: 0, y: 2)
//                    }
//                    HStack{
//                        Spacer()
//                        Text(verbatim: "Vous n'avez pas encore de compte?")
//                        Button(action: {
//                            //TODO
//                        }){
//                            Text("S'inscrire").foregroundColor(ColorManager.colorPrimary)
//                        }
//                        Spacer()
//                    }.padding(8).scaleEffect(0.8)
//                }
                Spacer()
                Spacer()
            }
        }
    }
}

struct WelcomeView_Previews: PreviewProvider {
    static var previews: some View {
        WelcomeView()
    }
}
