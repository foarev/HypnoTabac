//
//  ContentView.swift
//  HypnoTabac
//
//  Created by Valentin on 26/06/2020.
//  Copyright © 2020 HypnoTabac. All rights reserved.
//

import SwiftUI
import Firebase

struct ClientStatsView: View {
    @Binding var statsEnabled:Bool
    @Binding var navToMain:Bool
    @State var dataFetched:Bool = false
    @State var dataSetGrade : [barData] = []
    @State var dataSetCondition : [barData] = []
    @State var maxGrade : CGFloat = 0
    @State var maxCondition : CGFloat = 0
    @State var totalCigs = 0
    @State var avg : CGFloat = 0.0
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
                
                if(self.dataFetched){
                    ScrollView {
                        VStack {
                            Spacer()
                            Text("Vous avez consommé un total de \(totalCigs) cigarettes, soit environ \(String(format: "%.1f", avg)) par jour.").padding().font(.title)
                            Spacer()
                            BarChart(title: "Nombre de cigarettes en fonction de la note", dataSet: dataSetGrade, max: self.maxGrade)
                            Spacer()
                            BarChart(title: "Nombre de cigarettes en fonction de la condition", dataSet: dataSetCondition, max: self.maxCondition)
                            Spacer()
                        }
                    }
                } else {
                    Loader()
                }
                
                Spacer()
            }
            .onAppear{
                let userID = UserDefaults.standard.string(forKey: "userID")
                let hypnoID = UserDefaults.standard.string(forKey: "hypnoID")

                let dbRef = self.usersReference.child(hypnoID ?? "").child("clients").child(userID ?? "").child("cigs")

                dbRef.observeSingleEvent(of: .value, with: { (snapshot) in
                  // Get cig value
                    if(snapshot.value != nil){
                        let cigs =  snapshot.value as? Array<Any>
                        var numGrade = Array(repeating: 0, count: 3)
                        var numCondition = [String : Int]()
                        
                        var dateMax = DateComponents()
                        dateMax.calendar = Calendar.current
                        dateMax.year     = 1
                        dateMax.month    = 1
                        dateMax.day      = 1
                        
                        var dateMin = DateComponents()
                        dateMin.calendar = Calendar.current
                        dateMin.year     = 9999
                        dateMin.month    = 1
                        dateMin.day      = 1
                        
                        cigs!.forEach { c in
                            let cig = c as? NSDictionary
                            
                            var date = DateComponents()
                            date.calendar = Calendar.current
                            date.year = cig?["year"] as? Int
                            date.month = cig?["month"] as? Int
                            date.day = cig?["day"] as? Int
                            
                            if(date.date! > dateMax.date!){
                                dateMax.year = date.year
                                dateMax.month = date.month
                                dateMax.day = date.day
                            }
                            if(date.date! < dateMin.date!){
                                dateMin.year = date.year
                                dateMin.month = date.month
                                dateMin.day = date.day
                            }
                            
                            numGrade[(cig?["grade"] as! Int) - 1] += 1
                            
                            if numCondition[cig?["condition"] as! String] != nil {
                                numCondition[cig?["condition"] as! String] = numCondition[cig?["condition"] as! String]! + 1
                            } else {
                                numCondition[cig?["condition"] as! String] = 1
                            }
                            
                            self.totalCigs += 1
                        }
                        
                        let interval = Calendar.current.dateComponents([.day], from: dateMin.date!, to: dateMax.date!).day!
                        
                        self.avg = CGFloat(self.totalCigs)/CGFloat(interval)

                        var i = 0
                        numGrade.forEach { n in
                            self.dataSetGrade.append(barData(id: i, yValue: CGFloat(n), xLabel: "\(i+1)"))
                            if(CGFloat(n)>self.maxGrade){
                                self.maxGrade = CGFloat(n)
                            }
                            i += 1
                        }
                        
                        i = 0
                        numCondition.forEach { n in
                            self.dataSetCondition.append(barData(id: i, yValue: CGFloat(n.value), xLabel: n.key))
                            if(CGFloat(n.value)>self.maxCondition){
                                self.maxCondition = CGFloat(n.value)
                            }
                            i += 1
                        }
                        self.dataFetched = true
                    }
                    
                  }) { (error) in
                    print(error.localizedDescription)
                }
            }
        }
    }
}

struct ClientStatsView_Previews: PreviewProvider {
    static var previews: some View {
        ClientStatsView(statsEnabled: .constant(true), navToMain:.constant(false))
    }
}

struct BarChart : View {
    var title : String = ""
    var dataSet : [barData]
    var max : CGFloat
    var body: some View{
        VStack {
            Text(title).foregroundColor(Color.black.opacity(0.5)).font(.caption).padding()
            HStack(alignment: .bottom, spacing: 8){
                ForEach(dataSet){ i in
                    Bar(value : i.yValue, max: self.max, label: i.xLabel, count: CGFloat(self.dataSet.count))
                }
            }
        }
    }
}

struct Bar : View {
    @State var value : CGFloat = 0
    var max : CGFloat
    var label : String = ""
    var count : CGFloat = 0
    var body: some View{
        VStack {
            Text(String(format: "%.0f", Double(value))).foregroundColor(Color.black.opacity(0.5)).scaleEffect(0.8)
            Rectangle().fill(LinearGradient(gradient: Gradient(colors: [ColorManager.colorPrimary, ColorManager.colorPrimarySuperLight]), startPoint: .top, endPoint: .bottom)).frame(width:UIScreen.main.bounds.width / count - 12, height: value/max*100)
            VStack {
                Text(label).foregroundColor(Color.black.opacity(0.5))
                Spacer()
            }.frame(height:51)
        }
    }
}

struct barData : Identifiable {
    var id : Int
    var yValue : CGFloat
    var xLabel : String
}

//var dataSetGrade = [barData(id: 0, yPercent: 50, xLabel: "1"), barData(id: 1, yPercent: 20, xLabel: "2"), barData(id: 2, yPercent: 70, xLabel: "3")]
