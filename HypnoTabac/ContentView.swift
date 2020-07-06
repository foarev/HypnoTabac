//
//  ContentView.swift
//  HypnoTabac
//
//  Created by Valentin on 26/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var session: SessionStore
    var link:String
    
    func getUser(){
        session.listen()
    }
    
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
                /*if(self.link=="" && session.session == nil){
                    WelcomeView()
                } else */if(session.session != nil) {
                    ClientMainView()
                } else if(session.session == nil) {
                    ClientLoginView(link: link, email: .constant(""))
                }
            }.navigationBarTitle("")
            .navigationBarHidden(true)
            .navigationBarBackButtonHidden(true)
            .onAppear(perform: getUser)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(link:"").environmentObject(SessionStore())
    }
}
