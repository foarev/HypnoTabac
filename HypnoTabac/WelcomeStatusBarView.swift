//
//  StatusBarView.swift
//  HypnoTabac
//
//  Created by Valentin on 29/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct WelcomeStatusBarView: View {
    var body: some View {
        VStack{
            HStack{
                Spacer()
                Image("logo_trans").resizable().aspectRatio(contentMode: .fill).frame(width: 150.0,height: 150.0)
                Spacer()
            }.frame(height: 200.0).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimary2, ColorManager.colorPrimaryLight]), startPoint: .top, endPoint: .bottom))
            }
    }
}

struct WelcomeStatusBarView_Previews: PreviewProvider {
    static var previews: some View {
        WelcomeStatusBarView()
    }
}
