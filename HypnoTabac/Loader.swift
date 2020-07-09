//
//  Loader.swift
//  HypnoTabac
//
//  Created by Valentin on 08/07/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI

struct Loader: View {
    @State var animate = false
    var body: some View {
        VStack{
            Circle()
                .trim(from: 0.1, to: 0.8)
                .stroke(AngularGradient(gradient: .init(colors: [ColorManager.colorBackground, ColorManager.colorPrimary]), center: .center), style: StrokeStyle(lineWidth: 8, lineCap: .round))
                .frame(width: 50, height: 50)
                .rotationEffect(.init(degrees: self.animate ? 360 : 0))
                .animation(Animation.linear(duration: 0.8).repeatForever(autoreverses: false))
        }.onAppear{
            self.animate.toggle()
        }
    }
}

struct Loader_Previews: PreviewProvider {
    static var previews: some View {
        Loader()
    }
}
