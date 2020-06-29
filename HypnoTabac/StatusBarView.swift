//
//  StatusBarView.swift
//  HypnoTabac
//
//  Created by Valentin on 29/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct StatusBarView: View {
    var body: some View {
        VStack{
            HStack{
                Spacer()
                Image("logo_trans").resizable().aspectRatio(contentMode: .fill).frame(width: 90.0,height: 90.0)
                Spacer()
            }.frame(height: 80.0).background(LinearGradient(gradient: .init(colors: [ColorManager.colorPrimary2, ColorManager.colorPrimary]), startPoint: .top, endPoint: .bottom))
            }
    }
}

struct StatusBarView_Previews: PreviewProvider {
    static var previews: some View {
        StatusBarView()
    }
}
