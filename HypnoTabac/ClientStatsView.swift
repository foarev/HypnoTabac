//
//  ContentView.swift
//  HypnoTabac
//
//  Created by Valentin on 26/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct ClientStatsView: View {
    @Binding var statsEnabled:Bool
    @Binding var navToMain:Bool
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
                ZStack(alignment:.topLeading){
                    StatusBarView()
                    Button(action: {
                        self.statsEnabled.toggle()
                        self.navToMain.toggle()
                    }){
                        Image(systemName: "chevron.left").frame(height: 80.0).padding([.leading],20).foregroundColor(ColorManager.colorBackground)
                    }
                }
                Spacer()
            }
        }
    }
}

struct ClientStatsView_Previews: PreviewProvider {
    static var previews: some View {
        ClientStatsView(statsEnabled: .constant(true), navToMain:.constant(false))
    }
}
